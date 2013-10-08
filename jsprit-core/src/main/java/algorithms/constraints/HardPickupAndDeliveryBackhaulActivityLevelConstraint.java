package algorithms.constraints;

import algorithms.InsertionContext;
import algorithms.StateManager;
import algorithms.StateTypes;
import basics.route.DeliveryActivity;
import basics.route.PickupActivity;
import basics.route.ServiceActivity;
import basics.route.Start;
import basics.route.TourActivity;

public class HardPickupAndDeliveryBackhaulActivityLevelConstraint implements HardActivityLevelConstraint {
	
	private StateManager stateManager;
	
	public HardPickupAndDeliveryBackhaulActivityLevelConstraint(StateManager stateManager) {
		super();
		this.stateManager = stateManager;
	}

	@Override
	public boolean fulfilled(InsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
		if(newAct instanceof PickupActivity && nextAct instanceof DeliveryActivity){ return false; }
		if(newAct instanceof ServiceActivity && nextAct instanceof DeliveryActivity){ return false; }
		if(newAct instanceof DeliveryActivity && prevAct instanceof PickupActivity){ return false; }
		if(newAct instanceof DeliveryActivity && prevAct instanceof ServiceActivity){ return false; }
		int loadAtPrevAct;
		int futurePicks;
		int pastDeliveries;
		if(prevAct instanceof Start){
			loadAtPrevAct = (int)stateManager.getRouteState(iFacts.getRoute(), StateTypes.LOAD_AT_DEPOT).toDouble();
			futurePicks = (int)stateManager.getRouteState(iFacts.getRoute(), StateTypes.LOAD).toDouble();
			pastDeliveries = 0;
		}
		else{
			loadAtPrevAct = (int) stateManager.getActivityState(prevAct, StateTypes.LOAD).toDouble();
			futurePicks = (int) stateManager.getActivityState(prevAct, StateTypes.FUTURE_PICKS).toDouble();
			pastDeliveries = (int) stateManager.getActivityState(prevAct, StateTypes.PAST_DELIVERIES).toDouble();
		}
		if(newAct instanceof PickupActivity || newAct instanceof ServiceActivity){
			if(loadAtPrevAct + newAct.getCapacityDemand() + futurePicks > iFacts.getNewVehicle().getCapacity()){
				return false;
			}
		}
		if(newAct instanceof DeliveryActivity){
			if(loadAtPrevAct + Math.abs(newAct.getCapacityDemand()) + pastDeliveries > iFacts.getNewVehicle().getCapacity()){
				return false;
			}
			
		}
		return true;
	}
		
}