import java.util.function.Supplier;

@FunctionalInterface
public interface UUIDGenerator extends Supplier {
	UUID next();
	@Override
	UUID get();
}