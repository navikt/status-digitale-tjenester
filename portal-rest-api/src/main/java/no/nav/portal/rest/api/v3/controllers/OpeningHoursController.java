package no.nav.portal.rest.api.v3.controllers;

import nav.portal.core.repositories.OpeningHoursRepository;
import nav.portal.core.repositories.ServiceRepository;
import no.nav.portal.rest.api.EntityDtoMappers;
import no.nav.portal.rest.api.Helpers.OpeningHoursHelper;
import no.portal.web.generated.api.*;

import org.actioncontroller.*;
import org.actioncontroller.json.JsonBody;
import org.fluentjdbc.DbContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class OpeningHoursController {

    private OpeningHoursHelper openingHoursHelper;
    private final OpeningHoursRepository openingHoursRepository;

    public OpeningHoursController(DbContext dbContext) {
        this.openingHoursHelper = new OpeningHoursHelper(dbContext);
        this.openingHoursRepository = new OpeningHoursRepository(dbContext);
    }

    /*Delen av AreaController for Rule*/
    @POST("/OpeningHours/Rule")
    @JsonBody
    public OHRuleDto newRule(@JsonBody OHRuleDto oHRuleDto) {
        if(!openingHoursHelper.isValidRule(oHRuleDto)){
            throw new HttpRequestException("Rule not valid: "+ oHRuleDto.getRule());
        }
        return openingHoursHelper.saveNewRule(oHRuleDto);
    }

    @PUT("/OpeningHours/Rule/:Rule_id")
    @JsonBody
    public void updateRule(@PathParam("Rule_id") UUID rule_id, @JsonBody OHRuleDto oHRuleDto) {
        oHRuleDto.setId(rule_id);
        if(!openingHoursHelper.isValidRule(oHRuleDto)){
            throw new HttpRequestException("Rule not valid: "+ oHRuleDto.getRule());
        }
        openingHoursHelper.updateRule(oHRuleDto);
    }

    @DELETE("/OpeningHours/Rule/:Rule_id")
    @JsonBody
    public void deleteRule(@PathParam("Rule_id") UUID rule_id){
        openingHoursHelper.deleteRule(rule_id);
    }

    @GET("/OpeningHours/Rule/:Rule_id")
    @JsonBody
    public OHRuleDto getRule(@PathParam("Rule_id") UUID rule_id) {
        return openingHoursHelper.getRule(rule_id);
    }

    @GET("/OpeningHours/Rules")
    @JsonBody
    public List<OHRuleDto> getRules() {
        return EntityDtoMappers.toOpeningHoursRuleDto(openingHoursRepository.getAllOpeningHoursRules());
    }

    /*Delen av AreaController for Group*/
    @POST("/OpeningHours/Group")
    @JsonBody
    public OHGroupThinDto newGroup(@JsonBody OHGroupThinDto oHGroupThinDto) {
        return openingHoursHelper.saveGroup(oHGroupThinDto);
    }

    @PUT("/OpeningHours/Group/:Group_id")
    @JsonBody
    public void updateGroup(@PathParam("Group_id") UUID group_id, @JsonBody OHGroupThinDto oHGroupThinDto) {
        oHGroupThinDto.setId(group_id);
        openingHoursHelper.updateGroup(oHGroupThinDto);
    }

    @GET("/OpeningHours/Group")
    @JsonBody
    public List<OHGroupDto> getGroups() {
        return openingHoursHelper.getAllGroups();
    }

    @DELETE("/OpeningHours/Group/:Group_id")
    @JsonBody
    public void deleteGroup(@PathParam("Group_id") UUID group_id){
        openingHoursHelper.deleteGroup(group_id);
    }

    @GET("/OpeningHours/Group/:Group_id")
    @JsonBody
    public OHGroupDto getGroup(@PathParam("Group_id") UUID group_id) {
        return openingHoursHelper.getGroup(group_id);
    }

    @PUT("/OpeningHours/:Group_id/:Service_id")
    @JsonBody
    public void setOpeningHoursToService(@PathParam("Group_id") UUID group_id, @PathParam("Service_id") UUID service_id) {
        openingHoursHelper.setOpeningHoursToService(group_id, service_id);
    }

    @DELETE("/OpeningHours/Service/:Service_id")
    @JsonBody
    public void removeOpeningHoursFromService( @PathParam("Service_id") UUID service_id) {
        openingHoursRepository.removeOpeningHoursFromService(service_id);
    }

    @GET("/OpeningHours/Service/:Service_id")
    @JsonBody
    public OHGroupDto getOHGroupForService(@PathParam("Service_id") UUID service_id) {
        return openingHoursHelper.getOHGroupForService(service_id);
    }

    @GET("/OpeningHours/Service/:Service_id/:Date")
    @JsonBody
    public String getOpeningHoursForServiceOnDate(@PathParam("Service_id") UUID service_id, @PathParam("Date") String date) {
        return openingHoursHelper.getOpeningHoursForServiceOnDate(service_id,date);
    }

    @GET("/OpeningHours/Group/:Group_id/:Date")
    @JsonBody
    public String getOpeningHoursForGroupOnDate(@PathParam("Group_id") UUID group_id, @PathParam("Date") String date) {
        return openingHoursHelper.getOpeningHoursForGroupOnDate(group_id,date);
    }


}
