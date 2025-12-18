import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.esthree.awssdk {
	requires sirius.stellar.esthree;
	requires org.jspecify;

	requires static io.avaje.http.client;

	requires software.amazon.awssdk.services.s3;
	requires software.amazon.awssdk.auth;
	requires software.amazon.awssdk.regions;
	requires software.amazon.awssdk.awscore;
	requires software.amazon.awssdk.core;
	requires software.amazon.awssdk.http;

	exports sirius.stellar.esthree.awssdk;

	provides sirius.stellar.esthree.Esthree.Builder
		with sirius.stellar.esthree.awssdk.AwsEsthreeBuilder;
}