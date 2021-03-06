package jsprit.core.problem.constraint;

import jsprit.core.problem.Capacity;
import jsprit.core.problem.misc.JobInsertionContext;
import jsprit.core.problem.solution.route.activity.DeliverShipment;
import jsprit.core.problem.solution.route.activity.PickupShipment;
import jsprit.core.problem.solution.route.activity.Start;
import jsprit.core.problem.solution.route.activity.TourActivity;
import jsprit.core.problem.solution.route.state.RouteAndActivityStateGetter;
import jsprit.core.problem.solution.route.state.StateFactory;


/**
 * Constraint that ensures capacity constraint at each activity.
 * 
 * <p>This is critical to consistently calculate pd-problems with capacity constraints. Critical means
 * that is MUST be visited. It also assumes that pd-activities are visited in the order they occur in a tour.
 * 
 * @author schroeder
 *
 */
public class PickupAndDeliverShipmentLoadActivityLevelConstraint implements HardActivityStateLevelConstraint {
	
	private RouteAndActivityStateGetter stateManager;
	
	/**
	 * Constructs the constraint ensuring capacity constraint at each activity.
	 * 
	 * <p>This is critical to consistently calculate pd-problems with capacity constraints. Critical means
	 * that is MUST be visited. It also assumes that pd-activities are visited in the order they occur in a tour.
	 * 
	 * 
	 * @param stateManager
	 */
	public PickupAndDeliverShipmentLoadActivityLevelConstraint(RouteAndActivityStateGetter stateManager) {
		super();
		this.stateManager = stateManager;
	}
	
	/**
	 * Checks whether there is enough capacity to insert newAct between prevAct and nextAct.
	 * 
	 */
	@Override
	public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
		if(!(newAct instanceof PickupShipment) && !(newAct instanceof DeliverShipment)){
			return ConstraintsStatus.FULFILLED;
		}
		Capacity loadAtPrevAct;
//		int loadAtPrevAct;
		if(prevAct instanceof Start){
			loadAtPrevAct = stateManager.getRouteState(iFacts.getRoute(), StateFactory.LOAD_AT_BEGINNING, Capacity.class);
		}
		else{
			loadAtPrevAct = stateManager.getActivityState(prevAct, StateFactory.LOAD, Capacity.class);
		}
		if(newAct instanceof PickupShipment){
			if(!Capacity.addup(loadAtPrevAct, newAct.getSize()).isLessOrEqual(iFacts.getNewVehicle().getType().getCapacityDimensions())){
				return ConstraintsStatus.NOT_FULFILLED;
			}
//			if(loadAtPrevAct + newAct.getCapacityDemand() > iFacts.getNewVehicle().getCapacity()){
//				return ConstraintsStatus.NOT_FULFILLED;
//			}
		}
		if(newAct instanceof DeliverShipment){
			if(!Capacity.addup(loadAtPrevAct, Capacity.invert(newAct.getSize())).isLessOrEqual(iFacts.getNewVehicle().getType().getCapacityDimensions())){
				return ConstraintsStatus.NOT_FULFILLED_BREAK;
			}
//			if(loadAtPrevAct + Math.abs(newAct.getCapacityDemand()) > iFacts.getNewVehicle().getCapacity()){
//				return ConstraintsStatus.NOT_FULFILLED_BREAK;
//			}
		}
		return ConstraintsStatus.FULFILLED;
	}
	
		
}