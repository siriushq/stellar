package sirius.stellar.esthree;

import java.util.Arrays;
import java.util.Optional;

/// Enumeration of valid AWS regions. This is only provided for convenience.
public enum EsthreeRegion implements CharSequence {

	AF_SOUTH_1("af-south-1"),
	AP_EAST_1("ap-east-1"),
	AP_EAST_2("ap-east-2"),
	AP_NORTHEAST_1("ap-northeast-1"),
	AP_NORTHEAST_2("ap-northeast-2"),
	AP_NORTHEAST_3("ap-northeast-3"),
	AP_SOUTH_1("ap-south-1"),
	AP_SOUTH_2("ap-south-2"),
	AP_SOUTHEAST_1("ap-southeast-1"),
	AP_SOUTHEAST_2("ap-southeast-2"),
	AP_SOUTHEAST_3("ap-southeast-3"),
	AP_SOUTHEAST_4("ap-southeast-4"),
	AP_SOUTHEAST_5("ap-southeast-5"),
	AP_SOUTHEAST_6("ap-southeast-6"),
	AP_SOUTHEAST_7("ap-southeast-7"),

	CA_CENTRAL_1("ca-central-1"),
	CA_WEST_1("ca-west-1"),

	CN_NORTH_1("cn-north-1"),
	CN_NORTHWEST_1("cn-northwest-1"),

	EU_CENTRAL_1("eu-central-1"),
	EU_CENTRAL_2("eu-central-2"),
	EU_ISOE_WEST_1("eu-isoe-west-1"),
	EU_NORTH_1("eu-north-1"),
	EU_SOUTH_1("eu-south-1"),
	EU_SOUTH_2("eu-south-2"),
	EU_WEST_1("eu-west-1"),
	EU_WEST_2("eu-west-2"),
	EU_WEST_3("eu-west-3"),

	EUSC_DE_EAST_1("eusc-de-east-1"),

	IL_CENTRAL_1("il-central-1"),

	ME_CENTRAL_1("me-central-1"),
	ME_SOUTH_1("me-south-1"),

	MX_CENTRAL_1("mx-central-1"),

	SA_EAST_1("sa-east-1"),

	US_EAST_1("us-east-1"),
	US_EAST_2("us-east-2"),
	US_WEST_1("us-west-1"),
	US_WEST_2("us-west-2"),

	US_GOV_EAST_1("us-gov-east-1"),
	US_GOV_WEST_1("us-gov-west-1"),

	US_ISO_EAST_1("us-iso-east-1"),
	US_ISO_WEST_1("us-iso-west-1"),

	US_ISOB_EAST_1("us-isob-east-1"),
	US_ISOF_EAST_1("us-isof-east-1"),
	US_ISOF_SOUTH_1("us-isof-south-1"),

	AWS_CN_GLOBAL("aws-cn-global"),
	AWS_GLOBAL("aws-global"),
	AWS_ISO_B_GLOBAL("aws-iso-b-global"),
	AWS_ISO_E_GLOBAL("aws-iso-e-global"),
	AWS_ISO_F_GLOBAL("aws-iso-f-global"),
	AWS_ISO_GLOBAL("aws-iso-global"),
	AWS_US_GOV_GLOBAL("aws-us-gov-global");

	private final String identifier;

	EsthreeRegion(String identifier) {
		this.identifier = identifier;
	}

	/// Parse a [EsthreeRegion] from the provided string identifier.
	public static Optional<EsthreeRegion> from(String identifier) {
		return Arrays.stream(EsthreeRegion.values())
				.filter(it -> it.identifier.equals(identifier))
				.findFirst();
	}

	@Override
	public String toString() {
		return this.identifier;
	}

	@Override
	public int length() {
		return this.identifier.length();
	}

	@Override
	public char charAt(int index) {
		return this.identifier.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return this.identifier.subSequence(start, end);
	}
}