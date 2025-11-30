package sirius.stellar.annotation.intellij;

import com.intellij.psi.*;
import com.intellij.psi.augment.PsiAugmentProvider;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.*;
import static java.util.Collections.*;

/// Implementation of [PsiAugmentProvider] for augmenting any methods annotated
/// with annotations from `sirius.stellar.annotation`.
public final class DPsiAugmentProvider extends PsiAugmentProvider {

	private static final String
		CONTRACT = "sirius.stellar.annotation.Contract",
		JETBRAINS_CONTRACT = "org.jetbrains.annotations.Contract";

	private static final String
		EXPERIMENTAL = "sirius.stellar.annotation.Experimental",
		JETBRAINS_EXPERIMENTAL = "org.jetbrains.annotations.ApiStatus.Experimental";

	private static final String
		INTERNAL = "sirius.stellar.annotation.Internal",
		JETBRAINS_INTERNAL = "org.jetbrains.annotations.ApiStatus.Internal";

	@Override
	protected <T extends PsiElement>
	List<T> getAugments(PsiElement element, Class<T> type, @Nullable String hint) {
		if (!(element instanceof PsiModifierList list)) return emptyList();
		if (!(list.getParent() instanceof PsiMethod method)) return emptyList();
		if (!method.isPhysical() || type != PsiAnnotation.class) return emptyList();

		List<T> augments = new ArrayList<>();
		for (PsiElement child : list.getChildren()) {
			if (!(child instanceof PsiAnnotation childAnnotation)) continue;
			augmentContract(augments, method, childAnnotation);
			augmentInternal(augments, method, childAnnotation);
			augmentExperimental(augments, method, childAnnotation);
		}
		return augments;
	}

	/// Augments the method with a synthetic contract annotation, using
	/// [#JETBRAINS_CONTRACT], if the [#CONTRACT] annotation is found.
	@SuppressWarnings("unchecked")
	private <T extends PsiElement>
	void augmentContract(List<T> augments, PsiMethod method, PsiAnnotation annotation) {
		if (!CONTRACT.equals(annotation.getQualifiedName())) return;

		PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
		if (!(value instanceof PsiLiteralExpression literal)) return;
		if (literal.getValue() == null) return;

		String clause = String.valueOf(literal.getValue());
		PsiElementFactory factory = PsiElementFactory.getInstance(method.getProject());

		String source = format("@{0}(\"{1}\")", JETBRAINS_CONTRACT, clause);
		PsiAnnotation synthetic = factory.createAnnotationFromText(source, method);

		augments.add((T) synthetic);
	}

	/// Augments the method with a synthetic internal annotation, using
	/// [#JETBRAINS_INTERNAL], if the [#INTERNAL] annotation is found.
	@SuppressWarnings("unchecked")
	private <T extends PsiElement>
	void augmentInternal(List<T> augments, PsiMethod method, PsiAnnotation annotation) {
		if (!INTERNAL.equals(annotation.getQualifiedName())) return;

		PsiElementFactory factory = PsiElementFactory.getInstance(method.getProject());

		String source = format("@{0}", JETBRAINS_INTERNAL);
		PsiAnnotation synthetic = factory.createAnnotationFromText(source, method);

		augments.add((T) synthetic);
	}

	/// Augments the method with a synthetic experimental annotation, using
	/// [#JETBRAINS_EXPERIMENTAL], if the [#EXPERIMENTAL] annotation is found.
	@SuppressWarnings("unchecked")
	private <T extends PsiElement>
	void augmentExperimental(List<T> augments, PsiMethod method, PsiAnnotation annotation) {
		if (!EXPERIMENTAL.equals(annotation.getQualifiedName())) return;

		PsiElementFactory factory = PsiElementFactory.getInstance(method.getProject());

		String source = format("@{0}", JETBRAINS_EXPERIMENTAL);
		PsiAnnotation synthetic = factory.createAnnotationFromText(source, method);

		augments.add((T) synthetic);
	}
}