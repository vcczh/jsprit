package algorithms.constraints;

import algorithms.InsertionContext;
import basics.route.TourActivity;

public interface HardActivityLevelConstraint {
	
	public boolean fulfilled(InsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime);

}