package com.charliechiang.wastesortinghelperserver.model;

import com.charliechiang.wastesortinghelperserver.controller.ServerSettingsController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ServerSettingModelAssembler
        implements RepresentationModelAssembler<ServerSetting, EntityModel<ServerSetting>> {

    @Override
    public EntityModel<ServerSetting> toModel(ServerSetting serverSetting) {

        return EntityModel.of(serverSetting,
                              linkTo(methodOn(ServerSettingsController.class).getServerSettingSingle(serverSetting.getId())).withSelfRel());
    }
}
