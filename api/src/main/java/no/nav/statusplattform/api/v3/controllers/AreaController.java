package no.nav.statusplattform.api.v3.controllers;

import nav.statusplattform.core.repositories.AreaRepository;
import nav.statusplattform.core.repositories.SubAreaRepository;
import no.nav.statusplattform.api.EntityDtoMappers;
import no.nav.statusplattform.api.Helpers.AreaControllerHelper;
import no.nav.statusplattform.api.Helpers.SubAreaControllerHelper;
import no.nav.statusplattform.generated.api.AreaDto;
import no.nav.statusplattform.generated.api.IdContainerDto;
import no.nav.statusplattform.generated.api.ServiceDto;
import no.nav.statusplattform.generated.api.SubAreaDto;
import org.actioncontroller.DELETE;
import org.actioncontroller.GET;
import org.actioncontroller.HttpNotFoundException;
import org.actioncontroller.POST;
import org.actioncontroller.PUT;
import org.actioncontroller.PathParam;
import org.actioncontroller.json.JsonBody;
import org.fluentjdbc.DbContext;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AreaController {

   private final AreaRepository areaRepository;
   private final AreaControllerHelper areaControllerHelper;
   private final SubAreaRepository subAreaRepository;
   private final SubAreaControllerHelper subAreaControllerHelper;

   public AreaController(DbContext dbContext) {
      this.areaControllerHelper = new AreaControllerHelper(dbContext);
      this.areaRepository = new AreaRepository(dbContext);
      this.subAreaRepository = new SubAreaRepository(dbContext);
      this.subAreaControllerHelper = new SubAreaControllerHelper(dbContext);
   }


   @GET("/Areas/Minimal")
   @JsonBody
   public List<AreaDto> getAllAreasMinimal() {
      return areaControllerHelper.getAllAreasShallow();
   }

   @GET("/Areas/WithComponents/Minimal")
   @JsonBody
   public List<AreaDto> getAllAreasWithComponentsMinimal() {
      return areaControllerHelper.getAllAreasWithComponentsShallow();
   }


   @GET("/Areas")
   @JsonBody
   public List<AreaDto> getAllAreas() {
      return areaControllerHelper.getAllAreas();

   }

   @POST("/Areas")
   @JsonBody
   public IdContainerDto newArea(@JsonBody AreaDto areaDto) {
      UUID uuid = areaControllerHelper.newArea(areaDto).getId();
      return new IdContainerDto().id(uuid);
   }

   @PUT("/Area/:Area_id")
   @JsonBody
   public void updateArea(@PathParam("Area_id") UUID area_id, @JsonBody AreaDto areaDto ) {
      areaDto.setId(area_id);
      areaControllerHelper.updateArea(areaDto);
   }

   @DELETE("/Area/:Area_id")
   @JsonBody
   public void deleteArea(@PathParam("Area_id") UUID area_id ) {
      if(!areaRepository.deleteArea(area_id)){
         throw new HttpNotFoundException("Fant ikke område med id: " + area_id);
      }
   }


   @GET("/Areas/:Dashboard_id")
   @JsonBody
   public List<AreaDto> getAreas(@PathParam("Dashboard_id") UUID dashboard_id) {
      return areaControllerHelper.getAreasOnDashboard(dashboard_id);
   }

   @PUT("/Area/:Area_id/:Service_id")
   @JsonBody
   public void addServiceToArea(@PathParam("Area_id") UUID area_id, @PathParam("Service_id") UUID service_id ) {
      areaRepository.addServiceToArea(area_id,service_id);
   }

   @DELETE("/Area/:Area_id/:Service_id")
   @JsonBody
   public void removeServiceFromArea(@PathParam("Area_id") UUID area_id, @PathParam("Service_id") UUID service_id ) {
      areaRepository.removeServiceFromArea(area_id,service_id);
   }

   /*Delen av AreaController for SubArea*/

   @GET("/SubAreas")
   @JsonBody
   public List<SubAreaDto> getAllSubAreas() {
      return EntityDtoMappers.toSubAreaDtoDeep(subAreaRepository.retrieveAll());
   }

   @POST("/SubArea")
   @JsonBody
   public IdContainerDto newSubArea(@JsonBody SubAreaDto subAreaDto) {
      UUID uuid = subAreaControllerHelper.newSubArea(subAreaDto).getId();
      subAreaRepository.setServicesOnSubArea(uuid,
              subAreaDto.getServices().stream().map(
                      ServiceDto::getId).collect(Collectors.toList()));
      return new IdContainerDto().id(uuid);
   }




}
