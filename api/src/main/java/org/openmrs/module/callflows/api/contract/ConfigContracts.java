package org.openmrs.module.callflows.api.contract;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.openmrs.module.callflows.api.validate.annotation.UniqueName;

@UniqueName
public class ConfigContracts {

    @NotNull
    @Valid
    private List<ConfigContract> configContracts;

    @JsonCreator
    public ConfigContracts() {
    }

    @JsonCreator
    public ConfigContracts(List<ConfigContract> configContracts) {
        this.configContracts = configContracts;
    }

    public List<ConfigContract> getConfigContracts() {
        return configContracts;
    }
}
