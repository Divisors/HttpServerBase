public class HumanName {
	protected final String prefix;
	protected final char leadingInitial;
	protected final String firstName;
	protected final String nickName;
	protected final String middleName;
	protected final String familyName;
	protected final String suffix;
	protected int hash;
	public HumanName(String prefix, char leadingInitial, String firstName, String middleName, String familyName, String suffix) {
		this.prefix = prefix;
		//TODO finish
	}
	//TODO add getters
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof HumanName && other.hashCode() == this.hashCode()))
			return false;
		if (other == this)
			return true;
		HumanName otherName = (HumanName) other;
		return Objects.equals(this.prefix, otherName.prefix)
			&& Objects.equals(this.leadingInitial, other.leadingInitial)
			&& Objects.equals(this.firstName, other.firstName)
			&& Objects.equals(this.nickName, other.nickName)
			&& Objects.equals(this.middleName, other.middleName)
			&& Objects.equals(this.familyName, other.familyName)
			&& Objects.equals(this.suffix, other.suffix);
	}
	@Override
	public int hashCode() {
		if (hash == 0)
			synchronized (this) {
				//It could have changed while we were waiting for the monitor lock
				if (hash == 0)
					hash = Objects.hashCode(this.prefix, this.leadingInitial, this.firstName, this.nickName, this.middleName, this.familyName, this.suffix);
			}
		return this.hash;
	}
}