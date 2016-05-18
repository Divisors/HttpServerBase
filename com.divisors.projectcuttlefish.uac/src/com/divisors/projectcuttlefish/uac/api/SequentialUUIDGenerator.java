
public class SequentialUUIDGenerator implements UUIDGenerator {
	protected final AtomicLong msb;
	protected final AtomicLong lsb;
	public SequentialUUIDGenerator() {
		
	}
	public SequentialUUIDGenerator(long start) {
		this.msb = new AtomicLong(new Random().nextLong());
		this.lsb = new AtomicLong(start);
	}
	@Override
	public UUID next() {
		return new UUID(msb.get(), lsb.getAndIncrement());
	}
}