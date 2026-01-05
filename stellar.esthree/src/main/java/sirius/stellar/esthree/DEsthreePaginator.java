package sirius.stellar.esthree;

import io.avaje.http.client.BodyContent;
import io.avaje.http.client.HttpClientRequest;
import io.avaje.http.client.HttpClientResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/// A paginator implementation for [Document], which reads (converts) into
/// `T` using a conversion function. This utilizes [Iterator] and provides
/// [#stream()] to obtain a [Stream] view of it.
final class DEsthreePaginator<T> implements Iterator<T> {

	private final ThreadLocal<DocumentBuilder> parser;
	private final EsthreeSigner signer;

	private final String continuation;
	private final HttpClientRequest request;

	private final DEsthreePaginatorMeasurer measurer;
	private final DEsthreePaginatorReader<T> reader;

	/// The previous response body, used for retrieving the next continuation.
	private Document previous;

	/// The index of the response list that the cursor is currently placed at.
	private int index;

	/// The size of the list returned in the previous response.
	private int size;

	/// Instantiate this paginator, with the provided field name for obtaining
	/// continuation tokens (e.g. `ContinuationToken`, `NextContinuationToken`)
	/// from the provided [Document], using the provided function that converts
	/// each [Document] response to `T` instances.
	///
	/// @param parser Thread-local [DocumentBuilder], parses XML body responses.
	/// @param signer [EsthreeSigner] used for signing request body content.
	///
	/// @param continuation Field name used for obtaining continuation tokens
	/// from the root of a given [Document] (e.g. the names mentioned above).
	///
	/// @param request Request builder to reuse, appending `continuation-token`
	/// (and `max-...` for limiting) query parameters (to a cloned variant).
	///
	/// @see DEsthreePaginatorMeasurer
	/// @see DEsthreePaginatorReader
	DEsthreePaginator(ThreadLocal<DocumentBuilder> parser, EsthreeSigner signer, String continuation, HttpClientRequest request, DEsthreePaginatorMeasurer measurer, DEsthreePaginatorReader<T> reader) {
		this.parser = parser;
		this.signer = signer;

		this.continuation = continuation;
		this.request = request;

		this.measurer = measurer;
		this.reader = reader;

		this.previous = parser.get().newDocument();
	}

	@Override
	public boolean hasNext() {
		return (this.index < this.size)
				|| this.previous.getElementsByTagName(this.continuation).getLength() > 0
				|| (this.index == 0 && this.size == 0);
	}

	@Override
	public T next() {
		if (this.index < this.size) {
			if (EsthreeException.detected(this.previous)) throw EsthreeException.of(this.previous);
			T t = this.reader.apply(this.previous, this.index);
			this.index++;
			return t;
		}

		try (InputStream stream = this.nextResponse()
					.asInputStream()
					.body()) {
			return nextBody(stream);
		} catch (IOException | SAXException exception) {
			throw new IllegalStateException("Failed to parse next response body for paginated Esthree request", exception);
		}
	}

	/// Non-blocking analogous implementation of [#next].
	public CompletableFuture<T> nextFuture() {
		if (this.index < this.size) return supplyAsync(() -> {
			if (EsthreeException.detected(this.previous)) throw EsthreeException.of(this.previous);
			T t = this.reader.apply(this.previous, this.index);
			this.index++;
			return t;
		});

		return this.nextResponse()
				.async()
				.asInputStream()
				.thenApplyAsync(HttpResponse::body)
				.thenApplyAsync(stream -> {
					try (stream) {
						return nextBody(stream);
					} catch (IOException | SAXException exception) {
						throw new CompletionException("Failed to parse next response body for asynchronous paginated Esthree request", exception);
					}
				});
	}

	/// Execute the request and return the associated [HttpClientResponse].
	/// Used by [#next] and [#nextFuture].
	private HttpClientResponse nextResponse() {
		Node token = this.previous.getElementsByTagName(this.continuation).item(0);

		HttpClientRequest request = this.request.clone();
		request.queryParam("max-buckets", "1000");
		if (token != null) request.queryParam("continuation-token", token.getTextContent());

		try (EsthreeSigner signer = this.signer.acquire()) {
			signer.sign("GET", request, BodyContent.of(new byte[0]));
			return request.GET();
		}
	}

	/// Returns the first element, from reading the provided new [InputStream]
	/// (which can first be obtained by invoking [#nextResponse]).
	private T nextBody(InputStream stream) throws SAXException, IOException {
		this.previous = this.parser.get().parse(stream);
		if (EsthreeException.detected(this.previous)) throw EsthreeException.of(this.previous);

		this.size = this.measurer.apply(this.previous);
		this.index = 0;

		T t = this.reader.apply(this.previous, this.index);
		this.index++;

		return t;
	}

	/// Returns a view of this paginator as a [Stream].
	public Stream<T> stream() {
		Spliterator<T> spliterator = spliteratorUnknownSize(this, 0);
		return StreamSupport.stream(spliterator, false);
	}

	/// Returns a view of this paginator as a [Stream] of [CompletableFuture]s.
	public Stream<CompletableFuture<T>> streamFuture() {
		Iterator<CompletableFuture<T>> iteratorFuture = new DEsthreePaginatorFuture<>(this);
		Spliterator<CompletableFuture<T>> spliterator = spliteratorUnknownSize(iteratorFuture, 0);
		return StreamSupport.stream(spliterator, false);
	}
}

/// Wraps the given [DEsthreePaginator] (which is an `[Iterator] of `T`),
/// and returns an [Iterator] of [CompletableFuture]s of `T`, using the
/// [DEsthreePaginator#nextFuture()] method (for [Iterator#next()]).
final class DEsthreePaginatorFuture<T> implements Iterator<CompletableFuture<T>> {

	private final DEsthreePaginator<T> delegate;

	DEsthreePaginatorFuture(DEsthreePaginator<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean hasNext() {
		return this.delegate.hasNext();
	}

	@Override
	public CompletableFuture<T> next() {
		return this.delegate.nextFuture();
	}
}

/// Function for measuring how many elements are in a response body list.
/// @see DEsthreePaginator#DEsthreePaginator
@FunctionalInterface
interface DEsthreePaginatorMeasurer extends Function<Document, Integer> {
	Integer apply(Document document);
}

/// Function for reading the next element, as `T`, from the response body list.
/// @see DEsthreePaginator#DEsthreePaginator
@FunctionalInterface
interface DEsthreePaginatorReader<T> extends BiFunction<Document, Integer, T> {
	T apply(Document document, Integer integer);
}