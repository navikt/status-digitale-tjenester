package no.nav.portal.rest.api.v3.controllers;

import no.nav.portal.rest.api.Helpers.OpsControllerHelper;
import no.nav.portal.rest.api.Helpers.ServiceControllerHelper;
import no.portal.web.generated.api.OPSmessageDto;
import org.actioncontroller.DELETE;
import org.actioncontroller.GET;
import org.actioncontroller.POST;
import org.actioncontroller.PathParam;
import org.actioncontroller.json.JsonBody;
import org.fluentjdbc.DbContext;

import java.util.List;
import java.util.UUID;

public class OpsController {


    private OpsControllerHelper opsControllerHelper;

    public OpsController(DbContext dbContext) {
        this.opsControllerHelper = new OpsControllerHelper(dbContext);
    }

    @POST("/OpsMessage")
    @JsonBody
    public OPSmessageDto createOpsMessage(@JsonBody OPSmessageDto opsMessageDto) {
        return opsControllerHelper.newOps(opsMessageDto);
    }

    @GET("/OpsMessage")
    @JsonBody
    public List<OPSmessageDto> getAllOpsMessages() {
        return opsControllerHelper.getAllOpsMessages();
    }

    @DELETE("/OpsMessage/:Ops_id")
    @JsonBody
    public void deleteOpsMessage(@PathParam("Ops_id") UUID ops_id ) {
        opsControllerHelper.deleteOps(ops_id);
    }
}
