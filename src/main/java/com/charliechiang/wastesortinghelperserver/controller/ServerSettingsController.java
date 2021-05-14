package com.charliechiang.wastesortinghelperserver.controller;

import com.charliechiang.wastesortinghelperserver.exception.ResourceNotFoundException;
import com.charliechiang.wastesortinghelperserver.model.ServerSetting;
import com.charliechiang.wastesortinghelperserver.model.ServerSettingModelAssembler;
import com.charliechiang.wastesortinghelperserver.repository.ServerSettingsRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/settings")
public class ServerSettingsController {
    private final ServerSettingsRepository serverSettingsRepository;
    private final ServerSettingModelAssembler serverSettingModelAssembler;

    public ServerSettingsController(ServerSettingsRepository serverSettingsRepository,
                                    ServerSettingModelAssembler serverSettingModelAssembler) {
        this.serverSettingsRepository = serverSettingsRepository;
        this.serverSettingModelAssembler = serverSettingModelAssembler;
    }

    public static <Any> Any getServerSetting(String settingId,
                                             ServerSettingsRepository serverSettingsRepository) throws Exception {
        Optional<ServerSetting> referencedServerSetting = serverSettingsRepository.findById(settingId);

        if (referencedServerSetting.isEmpty()) {
            throw new ResourceNotFoundException("ServerSetting " + settingId + " not found!");
        }

        if (referencedServerSetting.get().getType().equals("integer")) {
            return ((Any) ((Long) (Long.parseLong(referencedServerSetting.get().getValue()))));
        }
        if (referencedServerSetting.get().getType().equals("decimal")) {
            return ((Any) ((Double) (Double.parseDouble(referencedServerSetting.get().getValue()))));
        }

        return null;
    }

    @GetMapping("")
    public CollectionModel<EntityModel<ServerSetting>> getServerSettingAll() {
        List<EntityModel<ServerSetting>> serverSettings =
                serverSettingsRepository.findAll()
                                        .stream()
                                        .map(serverSettingModelAssembler::toModel)
                                        .collect(Collectors.toList());

        return CollectionModel.of(serverSettings,
                                  linkTo(methodOn(ServerSettingsController.class).getServerSettingAll()).withSelfRel());
    }

    @GetMapping("/{settingId}")
    public EntityModel<ServerSetting> getServerSettingSingle(@PathVariable String settingId) {
        ServerSetting referencedServerSetting =
                serverSettingsRepository.findById(settingId)
                                        .orElseThrow(() -> new ResourceNotFoundException("ServerSetting with ID=" + settingId + " could not " +
                                                                                         "be " +
                                                                                         "found."));

        return serverSettingModelAssembler.toModel(referencedServerSetting);
    }

    @PostMapping("")
    public ResponseEntity<?> addServerSetting(@RequestBody ServerSetting newServerSetting) {

        EntityModel<ServerSetting> entityModel =
                serverSettingModelAssembler.toModel(serverSettingsRepository.save(newServerSetting));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/{settingId}")
    public ResponseEntity<?> deleteServerSetting(@PathVariable String settingId) {
        serverSettingsRepository.deleteById(settingId);

        return ResponseEntity.noContent().build();
    }
}
