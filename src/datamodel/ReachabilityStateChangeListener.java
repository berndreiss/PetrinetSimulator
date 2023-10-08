package datamodel;

public interface ReachabilityStateChangeListener {

	void onChange(ReachabilityState state);
	void onAdd(ReachabilityState state);
}
