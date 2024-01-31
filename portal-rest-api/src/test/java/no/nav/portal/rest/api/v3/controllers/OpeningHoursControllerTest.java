package no.nav.portal.rest.api.v3.controllers;

import nav.portal.core.entities.ServiceEntity;
import nav.portal.core.repositories.SampleData;
import nav.portal.core.repositories.TestDataSource;
import nav.portal.core.repositories.TestUtil;
import no.nav.portal.rest.api.EntityDtoMappers;
import no.portal.web.generated.api.*;
import org.actioncontroller.PathParam;
import org.actioncontroller.json.JsonBody;
import org.assertj.core.api.Assertions;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OpeningHoursControllerTest {
    private final DataSource dataSource = TestDataSource.create();
    private final DbContext dbContext = new DbContext();

    private DbContextConnection connection;

    private final OpeningHoursController openingHoursController = new OpeningHoursController(dbContext);
    private final ServiceController serviceController = new ServiceController(dbContext);

    @BeforeEach
    void startConnection() {
        connection = dbContext.startConnection(dataSource);
        TestUtil.clearAllTableData(dbContext);
    }

    @AfterEach
    void endConnection() {
        connection.close();
    }

   @Test
    void getRules() {
        //Arrange
        List<OHRuleDto> oHRulesDto = SampleDataDto.getRulesDto();
        List<OHRuleDto>savedOHRulesDto = new ArrayList<>();
        oHRulesDto.forEach(oHRuleDto -> {
            savedOHRulesDto.add(openingHoursController.newRule(oHRuleDto));
            oHRuleDto.setId(oHRuleDto.getId());
        });
        //Act
       List<OHRuleDto>retrievedOHRulesDto = openingHoursController.getRules();
       //Assert
       Assertions.assertThat(retrievedOHRulesDto.size()).isEqualTo(savedOHRulesDto.size());
    }

    @Test
    void newRule(){
        //Arrange
        OHRuleDto oHRuleDto = SampleDataDto.getRandomizedOHRuleDto();
        //Act
        OHRuleDto savedOHRuleDto = openingHoursController.newRule(oHRuleDto);
        oHRuleDto.setId(oHRuleDto.getId());
        OHRuleDto retrievedOHRuleDto = openingHoursController.getRule(oHRuleDto.getId());
        //Assert
        Assertions.assertThat(retrievedOHRuleDto).isEqualTo(oHRuleDto);
        Assertions.assertThat(retrievedOHRuleDto.getId()).isEqualTo(oHRuleDto.getId());
    }

    @Test
    void updateRule() {
        //Arrange
        List<OHRuleDto>oHRulesDtos = SampleDataDto.getNonEmptyListOfOHRuleDto(2);
        List<OHRuleDto>oHRulesDtoBefore = new ArrayList<>();
        oHRulesDtos.forEach(oHRuleDto -> {
            oHRulesDtoBefore.add(openingHoursController.newRule(oHRuleDto));
            oHRuleDto.setId(oHRuleDto.getId());
        });
        String nameBefore = oHRulesDtoBefore.get(0).getName();
        //Act
        oHRulesDtoBefore.get(0).setName(oHRulesDtoBefore.get(1).getName());
        openingHoursController.updateRule(oHRulesDtoBefore.get(0).getId(), oHRulesDtoBefore.get(1));
        List<OHRuleDto>oHRulesDtoAfter = openingHoursController.getRules();
        //Assert
        Assertions.assertThat(nameBefore).isNotEqualTo(oHRulesDtoBefore.get(1).getName());
        Assertions.assertThat(oHRulesDtoAfter.get(0).getName()).isEqualTo(oHRulesDtoAfter.get(1).getName());

    }

    @Test
    void deleteRule() {
        //Arrange
        List<OHRuleDto>oHRulesDtos = SampleDataDto.getNonEmptyListOfOHRuleDto(2);
        List<OHRuleDto>oHRulesDtoBefore = new ArrayList<>();
        oHRulesDtos.forEach(oHRuleDto -> {
            oHRulesDtoBefore.add(openingHoursController.newRule(oHRuleDto));
            oHRuleDto.setId(oHRuleDto.getId());
        });
        OHRuleDto ruleToBeDeleted = oHRulesDtoBefore.get(0);
        //Act
        openingHoursController.deleteRule(oHRulesDtoBefore.get(0).getId());
        List<OHRuleDto>oHRulesDtoAfter = openingHoursController.getRules();
        //Assert
        Assertions.assertThat(oHRulesDtoBefore.size()).isEqualTo(2);
        Assertions.assertThat(oHRulesDtoAfter.size()).isEqualTo(1);
        Assertions.assertThat(oHRulesDtoBefore).contains(ruleToBeDeleted);
        Assertions.assertThat(oHRulesDtoAfter).doesNotContain(ruleToBeDeleted);

    }

    @Test
    void getRule() {
        //Arrange
        List<OHRuleDto> oHRulesDto = SampleDataDto.getRulesDto();
        List<OHRuleDto>savedOHRulesDto = new ArrayList<>();
        oHRulesDto.forEach(oHRuleDto -> {
            savedOHRulesDto.add(openingHoursController.newRule(oHRuleDto));
            oHRuleDto.setId(oHRuleDto.getId());
        });
        //Act
        OHRuleDto retrievedOHRuleDto = openingHoursController.getRule(savedOHRulesDto.get(0).getId());
        //Assert
        Assertions.assertThat(retrievedOHRuleDto).isEqualTo(savedOHRulesDto.get(0));
        Assertions.assertThat(retrievedOHRuleDto.getId()).isEqualTo(savedOHRulesDto.get(0).getId());
    }

    /*@Test
    void getGroups() {
        //Arrange
        List<OHGroupThinDto> oHGroupsThinDto= SampleDataDto.getGroupsThinDto();
        List<OHGroupThinDto>savedOHGroupsThinDto = new ArrayList<>();
        oHGroupsThinDto.forEach(oHGroupThinDto -> {
            savedOHGroupsThinDto.add(openingHoursController.newGroup(oHGroupThinDto));
            oHGroupThinDto.setId(oHGroupThinDto.getId());
        });
        //Act
        List<OHGroupThinDto>retrievedOHOHGroupsThinDto = openingHoursController.getGroups();
        //Assert
        Assertions.assertThat(retrievedOHOHGroupsThinDto.size()).isEqualTo(savedOHGroupsThinDto.size());
    }*/

    @Test
    void newGroup(){
        //Arrange
        OHGroupThinDto oHGroupThinDto = SampleDataDto.getRandomizedOHGroupThinDto();
        OHGroupThinDto savedOHGroupThinDto = openingHoursController.newGroup(oHGroupThinDto);
        savedOHGroupThinDto.setId(savedOHGroupThinDto.getId());
        //Act
        OHGroupDto retrievedOHGroupThinDto = openingHoursController.getGroup(savedOHGroupThinDto.getId());
        //Assert
        Assertions.assertThat(retrievedOHGroupThinDto.getName().equals(savedOHGroupThinDto.getName()));
    }

    @Test
    void deleteGroup(){
        //Arrange
        OHGroupThinDto basicGroup = SampleDataDto.getBasicGroupThinDto();
        OHGroupThinDto savedBasicGroup = openingHoursController.newGroup(basicGroup);
        savedBasicGroup.setId(savedBasicGroup.getId());

        List<OHGroupThinDto> groups = SampleDataDto.getNonEmptyListOfOHGroupThinDto(2);
        List<OHGroupThinDto>savedGroups = new ArrayList<>();
        groups.forEach( group -> {
            savedGroups.add(openingHoursController.newGroup(group));
            group.setId(group.getId());
        });
        OHGroupThinDto groupToBeDeleted = savedGroups.get(0);
        List<UUID> savedGroupsId = new ArrayList<>();
        savedGroups.forEach(group->{
            savedGroupsId.add(group.getId());
        });
        basicGroup.setRules(savedGroupsId);
        openingHoursController.updateGroup(basicGroup.getId(),basicGroup);
        List<OHGroupDto> retrievedGroupsBefore = openingHoursController.getGroups();
        List<UUID> retrievedGroupsUUIDBefore = new ArrayList<>();
        retrievedGroupsBefore.forEach(retrievedGroupBefore ->retrievedGroupsUUIDBefore.add(retrievedGroupBefore.getId()));
        //Act
        openingHoursController.deleteGroup(groupToBeDeleted.getId());
        List<OHGroupDto> retrievedGroupsAfter = openingHoursController.getGroups();
        List<UUID> retrievedGroupsUUIDAfter = new ArrayList<>();
        retrievedGroupsAfter.forEach(retrievedGroupAfter ->retrievedGroupsUUIDAfter.add(retrievedGroupAfter.getId()));
        //Assert
        Assertions.assertThat(retrievedGroupsUUIDBefore).contains(groupToBeDeleted.getId());
        Assertions.assertThat(retrievedGroupsUUIDAfter).doesNotContain(groupToBeDeleted.getId());
    }

    @Test
    void addAGroupToGroup() {
        //Arrange
        List<OHGroupThinDto> oHGroupsThinDto = SampleDataDto.getNonEmptyListOfOHGroupThinDto(2);
        OHGroupThinDto oHGroupThinDto1 = openingHoursController.newGroup(oHGroupsThinDto.get(0));
        OHGroupThinDto oHGroupThinDto2 = openingHoursController.newGroup(oHGroupsThinDto.get(1));
        oHGroupThinDto1.setId(oHGroupThinDto1.getId());
        oHGroupThinDto2.setId(oHGroupThinDto2.getId());
        OHGroupDto retrievedBefore = openingHoursController.getGroup(oHGroupThinDto1.getId());
        List<OHGroupDto> retrievedRulesBefore = retrievedBefore.getRules();
        //Act
        List<UUID> rules = oHGroupThinDto1.getRules();
        if (rules.size() == 0) {
            rules = new ArrayList<>();
        }
        rules.add(oHGroupThinDto2.getId());
        oHGroupThinDto1.setRules(rules);
        openingHoursController.updateGroup(oHGroupThinDto1.getId(),oHGroupThinDto1);
        OHGroupDto retrievedAfter = openingHoursController.getGroup(oHGroupThinDto1.getId());
        List<OHGroupDto> retrievedRulesAfter = retrievedAfter.getRules();
        OHGroupDto retrievedAddedGroup = retrievedRulesAfter.get(0);
        //Assert
        Assertions.assertThat(retrievedRulesBefore).isEmpty();
        Assertions.assertThat(retrievedRulesAfter.size()).isEqualTo(1);
        Assertions.assertThat(retrievedBefore.getId()).isEqualTo(retrievedAfter.getId());
        Assertions.assertThat(retrievedAddedGroup.getId()).isEqualTo(oHGroupThinDto2.getId());
    }

    @Test
    void addGroupToService() {
        //Arrange
        /*Create service*/
        ServiceEntity service = SampleData.getRandomizedServiceEntity();
        ServiceDto serviceDto = serviceController.newService(EntityDtoMappers.toServiceDtoShallow(service));
        serviceDto.setId(serviceDto.getId());
        UUID serviceDtoID = serviceDto.getId();
        /*Create group*/
        OHGroupThinDto oHGroupThinDto = SampleDataDto.getBasicGroupThinDto();
        OHGroupThinDto savedOHGroupThinDto = openingHoursController.newGroup(oHGroupThinDto);
        savedOHGroupThinDto.setId(savedOHGroupThinDto.getId());
        //Act
        //openingHoursController.setOpeningHoursToService(oHGroupThinDto.getId(),serviceDto.getId());
        //OHGroupDto retrievedGroup = openingHoursController.getOHGroupForService(serviceDto.getId());
        //Assert
        //Assertions.assertThat(retrievedGroup.getId()).isEqualTo(savedOHGroupThinDto.getId());
    }

    /*@Test
    void deleteGroupToService() {

    }*/

}
