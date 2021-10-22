package org.openmrs.module.callflows.api.contract;

import org.codehaus.jackson.annotate.JsonCreator;
import org.openmrs.module.callflows.api.validate.annotation.UniqueName;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@UniqueName
public class ConfigContractList {

    @NotNull
    @Valid
    private List<ConfigContract> configContracts;

    @JsonCreator
    public ConfigContractList() {
    }

    @JsonCreator
    public ConfigContractList(List<ConfigContract> configContracts) {
        this.configContracts = configContracts;
    }

    public List<ConfigContract> getConfigContracts() {
        return configContracts;
    }
}
