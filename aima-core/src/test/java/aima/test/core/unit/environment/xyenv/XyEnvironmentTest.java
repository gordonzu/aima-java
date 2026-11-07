package aima.test.core.unit.environment.xyenv;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XyEnvironmentTest {
	private XYEnvironment env;
	private SimpleAgent<DynamicPercept, Action> a;

	@Before
	public void setUp() {
		env = new XYEnvironment(10, 12);
		a = new MockAgent<>();
		env.addObjectToLocation(a, new XYLocation(3, 4));
		env.addAgent(a);
	}

	@Test
	public void testAddObject() {
		Assert.assertEquals(1, env.getAgents().size());
		Assert.assertEquals(new XYLocation(3, 4), env.getCurrentLocationFor(a));
	}

	private interface EnvironmentObject {
	}

	private interface Agent<P, A> extends EnvironmentObject {
	}

	private interface Action {
	}

	private static class DynamicPercept {
	}

	private static class SimpleAgent<P, A> implements Agent<P, A> {
	}

	private static class MockAgent<P, A> extends SimpleAgent<P, A> {
	}

	private static class XYEnvironment {
		private final XYEnvironmentState envState;
		private final Set<Agent<? super DynamicPercept, ? extends Action>> agents = new LinkedHashSet<>();
		private final Set<EnvironmentObject> envObjects = new LinkedHashSet<>();

		XYEnvironment(int width, int height) {
			assert width > 0;
			assert height > 0;
			envState = new XYEnvironmentState(width, height);
		}

		void addObjectToLocation(EnvironmentObject object, XYLocation location) {
			moveObjectToAbsoluteLocation(object, location);
		}

		void moveObjectToAbsoluteLocation(EnvironmentObject object, XYLocation location) {
			envState.moveObjectToAbsoluteLocation(object, location);
			addEnvironmentObject(object);
		}

		void addAgent(Agent<? super DynamicPercept, ? extends Action> agent) {
			agents.add(agent);
			addEnvironmentObject(agent);
		}

		void addEnvironmentObject(EnvironmentObject object) {
			envObjects.add(object);
		}

		List<Agent<?, ?>> getAgents() {
			return new ArrayList<>(agents);
		}

		XYLocation getCurrentLocationFor(EnvironmentObject object) {
			return envState.getCurrentLocationFor(object);
		}
	}

	private static class XYEnvironmentState {
		private final Map<XYLocation, Set<EnvironmentObject>> objsAtLocation = new LinkedHashMap<>();

		XYEnvironmentState(int width, int height) {
			for (int h = 1; h <= height; h++) {
				for (int w = 1; w <= width; w++) {
					objsAtLocation.put(new XYLocation(h, w), new LinkedHashSet<>());
				}
			}
		}

		void moveObjectToAbsoluteLocation(EnvironmentObject object, XYLocation location) {
			for (Set<EnvironmentObject> objects : objsAtLocation.values()) {
				if (objects.remove(object)) {
					break;
				}
			}
			getObjectsAt(location).add(object);
		}

		Set<EnvironmentObject> getObjectsAt(XYLocation location) {
			return objsAtLocation.computeIfAbsent(location, key -> new LinkedHashSet<>());
		}

		XYLocation getCurrentLocationFor(EnvironmentObject object) {
			for (XYLocation location : objsAtLocation.keySet()) {
				if (objsAtLocation.get(location).contains(object)) {
					return location;
				}
			}
			return null;
		}
	}

	private static class XYLocation {
		private final int x;
		private final int y;

		XYLocation(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object other) {
			if (other != null && getClass() == other.getClass()) {
				XYLocation location = (XYLocation) other;
				return x == location.x && y == location.y;
			}
			return false;
		}

		@Override
		public int hashCode() {
			int result = 17;
			result = 37 * result + x;
			result = 43 * result + y;
			return result;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}
}
