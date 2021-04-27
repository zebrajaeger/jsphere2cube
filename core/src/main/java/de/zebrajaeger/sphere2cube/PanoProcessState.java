package de.zebrajaeger.sphere2cube;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PanoProcessState extends Stringable {
    @JsonProperty
    private File rootFolder;
    @JsonProperty
    private List<Step> steps = new LinkedList<>();

    public enum StepType {
        READ_SOURCE_IMAGE,
        CALCULATE_VIEW,
        PREVIEW_CUBIC,
        PREVIEW_SCALED,
        PREVIEW_EQUIRECTANGULAR,
        FACE,
        SAVE_FACE,
        SAVE_TILES,
        SCALE_LEVEL,
        VIEWER_PANNELLUM,
        VIEWER_MARZIPANO,
        ARCHIVE,
        FINISHED
    }

    public enum ValueType {
        FILE, LEVEL_INDEX, FACE, VIEW_CALCULATOR, DURATION_MS, DURATION_HUMAN
    }

    public static class Step extends Stringable {
        @JsonProperty
        private StepType stepType;
        @JsonProperty
        private Map<ValueType, Object> values = new HashMap<>();


        public static Step of(StepType stepType) {
            return new Step(stepType);
        }

        public Step(StepType stepType) {
            this.stepType = stepType;
        }

        public Step with(ValueType key, Object value) {
            values.put(key, value);
            return this;
        }
    }

    public PanoProcessState(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public boolean isFinished() {
        return steps.stream().anyMatch(step -> StepType.FINISHED.equals(step.stepType));
    }
}


