package cc.aidshack.module.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DecimalSetting extends Setting{

    private double min,max,increment;

    @Expose
    @SerializedName("value")
    private double value;

    public DecimalSetting(String name, double min, double max, double defaultValue, double increment) {
        super(name);

        this.min = min;
        this.max = max;
        this.value = defaultValue;
        this.increment = increment;
    }
    public static double clamp(double value, double min, double max){
        value = Math.max(min, value);
        value = Math.min(max, value);
        return value;
    }

    public double getValue() {
        return value;
    }
    public float getValueFloat() {
        return (float)value;
    }
    public int getValueInt() {
        return (int)value;
    }

    public double getIncrement() {
        return increment;
    }

    public void setValue(double value) {
        value = clamp(value, this.min, this.max);
        value = Math.round(value * (1.0 /this.increment)) / (1.0 / this.increment);
        this.value = value;
    }
    public void increment(boolean positive){
        if (positive) {
            setValue(getValue() + getIncrement());
        }
        if (!positive) {
            setValue(getValue() - getIncrement());
        }
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
