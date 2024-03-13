package no.nav.statusplattform.api.v3.controllers;

import nav.statusplattform.core.entities.RecordEntity;
import nav.statusplattform.core.entities.ServiceEntity;
import nav.statusplattform.core.repositories.*;
import no.nav.statusplattform.api.EntityDtoMappers;
import no.nav.statusplattform.generated.api.*;
import org.assertj.core.api.Assertions;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class RecordControllerTest {
    private final DataSource dataSource = TestDataSource.create();
    private final DbContext dbContext = new DbContext();

    private DbContextConnection connection;

    private final DashboardController dashboardController = new DashboardController(dbContext);
    private final AreaController areaController = new AreaController(dbContext);
    private final ServiceController serviceController = new ServiceController(dbContext);
    private final RecordController recordController = new RecordController(dbContext);
    private final ServiceRepository serviceRepository = new ServiceRepository(dbContext);

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
    void addServiceStatus() {
        //Arrange
        AreaDto areaDto = SampleDataDto.getRandomizedAreaDto();
        IdContainerDto idContainerDto = areaController.newArea(areaDto);
        areaDto.setId(idContainerDto.getId());

        ServiceDto serviceDto = SampleDataDto.getRandomizedServiceDto();
        ServiceDto savedServiceDto = serviceController.newService(serviceDto);
        serviceDto.setId(savedServiceDto.getId());

        DashboardDto dashboardDto = SampleDataDto.getRandomizedDashboardDto();
        dashboardDto.setAreas(List.of(areaDto));
        IdContainerDto dashboardIdContainerDto = dashboardController.postDashboard(dashboardDto);
        dashboardDto.setId(dashboardIdContainerDto.getId());

        RecordDto recordDto = SampleDataDto.getRandomizedRecordDto();
        recordDto.setId(recordDto.getId());
        recordDto.setServiceId(serviceDto.getId());

        List<RecordDto> recordStatusOnServiceBefore = recordController.getRecordHistory(serviceDto.getId());

        //Act
        recordController.addServiceStatus(recordDto);
        areaController.addServiceToArea(areaDto.getId(), serviceDto.getId());
        dashboardController.getDashboard(dashboardDto.getId());

        //Assert
        List<AreaDto> retrievedAreas = areaController.getAllAreas();
        List<ServiceDto>retrievedServices = retrievedAreas.get(0).getServices();
        List<RecordDto> recordStatusOnServiceAfter = recordController.getRecordHistory(serviceDto.getId());

        Assertions.assertThat(recordStatusOnServiceBefore.size()).isEqualTo(1);
        Assertions.assertThat(recordStatusOnServiceBefore.get(0).getStatus()).isEqualTo(StatusDto.UNKNOWN);

        Assertions.assertThat(recordStatusOnServiceAfter.size()).isEqualTo(2);
        Assertions.assertThat(recordStatusOnServiceAfter.get(1).getServiceId()).isEqualTo(serviceDto.getId());
        Assertions.assertThat(retrievedAreas.get(0).getServices().size()).isEqualTo(1);
        Assertions.assertThat(retrievedServices.get(0).getId()).isEqualTo(serviceDto.getId());
    }

    @Test
    void getRecordHistory() {
        //Arrange
        ServiceEntity serviceEntity = SampleData.getRandomizedServiceEntity();
        serviceEntity.setId(serviceRepository.save(serviceEntity));
        UUID serviceID = serviceEntity.getId();
        List<RecordEntity> records = new ArrayList<>();
        records.add(SampleData.getRandomizedRecordEntityForService(serviceEntity));
        records.add(SampleData.getRandomizedRecordEntityForService(serviceEntity));
        records.add(SampleData.getRandomizedRecordEntityForService(serviceEntity));
        records.add(SampleData.getRandomizedRecordEntityForService(serviceEntity));
        records.add(SampleData.getRandomizedRecordEntityForService(serviceEntity));
        int i = 2;
        for(RecordEntity record : records){
            record.setCreated_at(ZonedDateTime.now().minusDays(i).truncatedTo(ChronoUnit.SECONDS));
            record.setId(RecordRepository.saveRecordBackInTime(record,dbContext));
            i++;
        }
        //Act
        List<RecordDto> retrievedRecordDtos = recordController.getRecordHistory(serviceID);
        //Assert
        List<RecordEntity> retrievedRecords = retrievedRecordDtos
                .stream().map(EntityDtoMappers::toRecordEntity).collect(Collectors.toList());
        //Assert
        Assertions.assertThat(retrievedRecordDtos.size()).isEqualTo(records.size());
        Assertions.assertThat(retrievedRecords).containsAll(records);
    }
}
