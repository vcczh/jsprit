package jsprit.core.algorithm.recreate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import jsprit.core.algorithm.recreate.listener.InsertionListeners;
import jsprit.core.algorithm.state.StateManager;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.constraint.ConstraintManager;
import jsprit.core.problem.constraint.HardActivityStateLevelConstraint;
import jsprit.core.problem.constraint.HardRouteStateLevelConstraint;
import jsprit.core.problem.cost.VehicleRoutingActivityCosts;
import jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import jsprit.core.problem.driver.Driver;
import jsprit.core.problem.driver.DriverImpl;
import jsprit.core.problem.job.Delivery;
import jsprit.core.problem.job.Pickup;
import jsprit.core.problem.job.Shipment;
import jsprit.core.problem.misc.JobInsertionContext;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.activity.TourActivity;
import jsprit.core.problem.solution.route.state.RouteAndActivityStateGetter;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.util.CostFactory;

import org.junit.Before;
import org.junit.Test;


public class ServiceInsertionAndLoadConstraintsTest {
	
	VehicleRoutingTransportCosts routingCosts;
	
	VehicleRoutingActivityCosts activityCosts = new VehicleRoutingActivityCosts(){

		@Override
		public double getActivityCost(TourActivity tourAct, double arrivalTime,Driver driver, Vehicle vehicle) {
			return 0;
		}
		
	};
	
	HardActivityStateLevelConstraint hardActivityLevelConstraint = new HardActivityStateLevelConstraint() {
		
		@Override
		public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct,TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
			return ConstraintsStatus.FULFILLED;
		}
	};
	
	HardRouteStateLevelConstraint hardRouteLevelConstraint = new HardRouteStateLevelConstraint(){

		@Override
		public boolean fulfilled(JobInsertionContext insertionContext) {
			return true;
		}
		
	};
	
	ActivityInsertionCostsCalculator activityInsertionCostsCalculator;
	
	ShipmentInsertionCalculator insertionCalculator;
	
	Vehicle vehicle;
	
	@Before
	public void doBefore(){
		routingCosts = CostFactory.createManhattanCosts();
		VehicleType type = VehicleTypeImpl.Builder.newInstance("t").addCapacityDimension(0, 2).setCostPerDistance(1).build();
		vehicle = VehicleImpl.Builder.newInstance("v").setStartLocationId("0,0").setType(type).build();
		activityInsertionCostsCalculator = new LocalActivityInsertionCostsCalculator(routingCosts, activityCosts);
		createInsertionCalculator(hardRouteLevelConstraint);
	}

	private void createInsertionCalculator(HardRouteStateLevelConstraint hardRouteLevelConstraint) {
		ConstraintManager constraintManager = new ConstraintManager(mock(VehicleRoutingProblem.class), mock(RouteAndActivityStateGetter.class));
		constraintManager.addConstraint(hardRouteLevelConstraint);
		insertionCalculator = new ShipmentInsertionCalculator(routingCosts, activityInsertionCostsCalculator, constraintManager);
	}
	
	@Test
	public void whenInsertingServiceWhileNoCapIsAvailable_itMustReturnTheCorrectInsertionIndex(){
		Delivery delivery = (Delivery) Delivery.Builder.newInstance("del").addSizeDimension(0, 41).setLocationId("10,10").build();
		Pickup pickup = (Pickup) Pickup.Builder.newInstance("pick").addSizeDimension(0, 15).setLocationId("0,10").build();
		
		VehicleType type = VehicleTypeImpl.Builder.newInstance("t").addCapacityDimension(0, 50).setCostPerDistance(1).build();
		Vehicle vehicle = VehicleImpl.Builder.newInstance("v").setStartLocationId("0,0").setType(type).build();
		
		VehicleRoute route = VehicleRoute.emptyRoute();
		route.setVehicleAndDepartureTime(vehicle, 0.0);
		
		Inserter inserter = new Inserter(new InsertionListeners());
		
		inserter.insertJob(delivery, new InsertionData(0,0,0,vehicle,null), route);
		
		VehicleRoutingProblem vrp = mock(VehicleRoutingProblem.class);
		
		StateManager stateManager = new StateManager(vrp.getTransportCosts());
		stateManager.updateLoadStates();
		
		ConstraintManager constraintManager = new ConstraintManager(vrp,stateManager);
		constraintManager.addLoadConstraint();
//		constraintManager.addConstraint(new PickupAndDeliverShipmentLoadActivityLevelConstraint(stateManager),Priority.CRITICAL);
//		constraintManager.addConstraint(new ShipmentPickupsFirstConstraint(),Priority.CRITICAL);
		
		stateManager.informInsertionStarts(Arrays.asList(route), null);
		
		JobCalculatorSwitcher switcher = new JobCalculatorSwitcher();
		ServiceInsertionCalculator serviceInsertionCalc = new ServiceInsertionCalculator(routingCosts, activityInsertionCostsCalculator, constraintManager);
		ShipmentInsertionCalculator insertionCalculator = new ShipmentInsertionCalculator(routingCosts, activityInsertionCostsCalculator, constraintManager);
		switcher.put(Pickup.class, serviceInsertionCalc);
		switcher.put(Delivery.class, serviceInsertionCalc);
		switcher.put(Shipment.class, insertionCalculator);
		
//		Pickup service = (Pickup)Pickup.Builder.newInstance("pick", 1).setLocationId("5,5").build();
		InsertionData iData = switcher.getInsertionData(route, pickup, vehicle, 0, DriverImpl.noDriver(), Double.MAX_VALUE);
//		routeActVisitor.visit(route);
		
		assertEquals(1, iData.getDeliveryInsertionIndex());
	}
	
}
