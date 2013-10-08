package algorithms.states;

import algorithms.StateManagerImpl;
import algorithms.StateManagerImpl.StateImpl;
import algorithms.StateTypes;
import basics.Delivery;
import basics.Job;
import basics.Pickup;
import basics.Service;
import basics.algo.JobInsertedListener;
import basics.route.VehicleRoute;

/**
 * Updates loads at start and end of a route if a job has been inserted in that route.
 * 
 * <p>These states can be retrieved by <br> 
 * stateManager.getRouteState(route, StateTypes.LOAD_AT_DEPOT) for LOAD_AT_DEPOT and <br>
 * stateManager.getRouteState(route, StateTypes.LOAD) for LOAD (i.e. load at end)
 * 
 * @param stateManager
 */
public class UpdateLoadsAtStartAndEndOfRouteWhenJobHasBeenInserted implements JobInsertedListener {

		private StateManagerImpl stateManager;
		
		/**
		 * Updates loads at start and end of a route if a job has been inserted in that route.
		 * 
		 * <p>These states can be retrieved by <br> 
		 * stateManager.getRouteState(route, StateTypes.LOAD_AT_DEPOT) for LOAD_AT_DEPOT and <br>
		 * stateManager.getRouteState(route, StateTypes.LOAD) for LOAD (i.e. load at end)
		 * 
		 * @param stateManager
		 */
		public UpdateLoadsAtStartAndEndOfRouteWhenJobHasBeenInserted(StateManagerImpl stateManager) {
			super();
			this.stateManager = stateManager;
		}

		@Override
		public void informJobInserted(Job job2insert, VehicleRoute inRoute, double additionalCosts, double additionalTime) {
			if(job2insert instanceof Delivery){
				int loadAtDepot = (int) stateManager.getRouteState(inRoute, StateTypes.LOAD_AT_DEPOT).toDouble();
//				log.info("loadAtDepot="+loadAtDepot);
				stateManager.putRouteState(inRoute, StateTypes.LOAD_AT_DEPOT, new StateImpl(loadAtDepot + job2insert.getCapacityDemand()));
			}
			else if(job2insert instanceof Pickup || job2insert instanceof Service){
				int loadAtEnd = (int) stateManager.getRouteState(inRoute, StateTypes.LOAD).toDouble();
//				log.info("loadAtEnd="+loadAtEnd);
				stateManager.putRouteState(inRoute, StateTypes.LOAD, new StateImpl(loadAtEnd + job2insert.getCapacityDemand()));
			}
		}
		
	}