package de.zebrajaeger.sphere2cube;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class PanoProcessState {

  @JsonProperty
  private File rootFolder;
  @JsonProperty
  private List<Step> steps = new LinkedList<>();

  public enum StepType {
    ENSURE_OUTPUT_FOLDER,
    READ_SOURCE_IMAGE,
    VIEW_CALCULATOR,
    SOURCE,
    CALCULATE_VIEW,
    DESCRIPTION_READ,
    DESCRIPTION_WRITE_SOURCE,
    DESCRIPTION_WRITE_TARGET,
    PREVIEW_CUBIC,
    PREVIEW_SCALED,
    PREVIEW_EQUIRECTANGULAR,
    FACE,
    SAVE_FACE,
    SAVE_TILES,
    SCALE_LEVEL,
    VIEWER_PANNELLUM_PREPARE,
    VIEWER_PANNELLUM_HTML,
    VIEWER_PANNELLUM_HTML_TEMPLATE,
    VIEWER_MARZIPANO_PREPARE,
    VIEWER_MARZIPANO_HTML,
    VIEWER_MARZIPANO_HTML_TEMPLATE,
    ARCHIVE,
    LAST_RUN_FILE,
    FINISHED
  }

  public enum ValueType {
    SOURCE_FILE,
    TARGET_FILE,
    LEVEL_INDEX,
    FACE,
    VIEW_CALCULATOR,
    DURATION_MS,
    DURATION_HUMAN
  }

  @Getter
  @ToString
  public static class Step {

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


