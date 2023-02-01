package cc.aidshack.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum MathUtils {
	;
	public static double roundToStep(double value, double step) {
		return step * Math.round(value / step);
	}
		public static int clamp(int value, int min, int max) {
			if (value < min) return min;
			return Math.min(value, max);
		}

		public static float clamp(float value, float min, float max) {
			if (value < min) return min;
			return Math.min(value, max);
		}

		public static double clamp(double value, double min, double max) {
			if (value < min) return min;
			return Math.min(value, max);
	}

	public static double round(double num, double increment) {
		if (increment < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(num);
		bd = bd.setScale((int) increment, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double dX = x2 - x1;
		double dY = y2 - y1;
		double dZ = z2 - z1;
		return dX * dX + dY * dY + dZ * dZ;
	}
}
