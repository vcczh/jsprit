package algorithms.constraints;

import java.util.ArrayList;
import java.util.Collection;

import algorithms.InsertionContext;

class HardRouteLevelConstraintManager implements HardRouteLevelConstraint {

	private Collection<HardRouteLevelConstraint> hardConstraints = new ArrayList<HardRouteLevelConstraint>();
	
	public void addConstraint(HardRouteLevelConstraint constraint){
		hardConstraints.add(constraint);
	}

	@Override
	public boolean fulfilled(InsertionContext insertionContext) {
		for(HardRouteLevelConstraint constraint : hardConstraints){
			if(!constraint.fulfilled(insertionContext)){
				return false;
			}
		}
		return true;
	}
	
}