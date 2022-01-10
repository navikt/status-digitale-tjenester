package no.nav.portal.rest.api.v3.controllers;

import nav.portal.core.entities.RecordEntity;
import nav.portal.core.entities.ServiceEntity;
import nav.portal.core.repositories.*;
import no.nav.portal.rest.api.EntityDtoMappers;
import no.portal.web.generated.api.ServiceDto;

import org.actioncontroller.PathParam;
import org.actioncontroller.json.JsonBody;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.junit.jupiter.api.AfterEach;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;


class ServiceControllerTest {

    private final SampleData sampleData = new SampleData();
    private final DataSource dataSource = TestDataSource.create();
    private final DbContext dbContext = new DbContext();

    private DbContextConnection connection;

    private final ServiceController serviceController = new ServiceController(dbContext);
    private final ServiceRepository serviceRepository = new ServiceRepository(dbContext);
    private final RecordRepository recordRepository = new RecordRepository(dbContext);


    @BeforeEach
    void startConnection() {
        connection = dbContext.startConnection(dataSource);
    }

    @AfterEach
    void endConnection() {
        TestUtil.clearAllTableData(dbContext);
        connection.close();
    }

    @Test
    void getServices() {
        //Arrange
        List<ServiceEntity> services = sampleData.getNonEmptyListOfServiceEntity(3);

        //Lagrer tjenester
        services.forEach(s -> s.setId(serviceRepository.save(s)));

        Map<UUID, RecordEntity> servicesWithStatus= new HashMap<>();

        //Lager tilfeldig status for hver tjeneste
        services.forEach(s -> servicesWithStatus.put(s.getId(), SampleData.getRandomizedRecordEntityForService(s)));
        //Lagrer statusen på tjenesten
        servicesWithStatus.keySet().forEach(id -> recordRepository.save(servicesWithStatus.get(id)));


        //TODO legg in avhengigheter her før mappingen:
        //serviceRepository.addDependencyToService();
        //Under bygges forventet dtoer m status og avhengigheter utifra oppsettet over:
        List<ServiceDto> expectedDtos = services.stream()
                .map(s->
                        EntityDtoMappers.toServiceDtoDeep(s, Collections.emptyList()))
                .map(dto -> setStatus(servicesWithStatus, dto))
                .collect(Collectors.toList());

        //TODO Orlene: Legge til avhengigheter og statuser på tjenestene
        // Først lagre avhengigheter til repository
        ServiceEntity service = sampleData.getRandomizedServiceEntityWithNameNotInList(services);
        service.setId(serviceRepository.save(service));
        RecordEntity serviceRecord = sampleData.getRandomizedRecordEntityForService(service);

        // Legge til avhengighetene i mappingen, se der det står Collections.emptyList() -> Liste av avhengigheter
        serviceRepository.addDependencyToService(service, services);

        //recordRepository.save()
        UUID serviceRecId = recordRepository.save(serviceRecord);
        //Act
        List<ServiceDto> resultingDtos = serviceController.getServices();

        //Assert
        Assertions.assertThat(resultingDtos).containsAll(expectedDtos);


    }

    private ServiceDto setStatus(Map<UUID, RecordEntity> servicesWithStatus, ServiceDto dto) {
        dto.setStatus(EntityDtoMappers.toStatusDto(servicesWithStatus.get(dto.getId())));
        return dto;
    }


    @Test
    void getService() {
        //Arrange
        ServiceEntity service = sampleData.getRandomizedServiceEntity();
        UUID serviceId = serviceRepository.save(service);
        service.setId(serviceId);
        RecordEntity serviceRecord = sampleData.getRandomizedRecordEntityForService(service);
        UUID serviceRecId = recordRepository.save(serviceRecord);
        //Act
        ServiceDto serviceDto = serviceController.getService(serviceId);
        //Assert
        Assertions.assertThat(serviceDto).isNotNull();
        Assertions.assertThat(serviceDto.getId()).isNotNull();
    }

    @Test
    void newService() {
        //Arrange
        ServiceEntity service = sampleData.getRandomizedServiceEntity();
        //Act
        ServiceDto serviceDto = serviceController.newService(EntityDtoMappers.toServiceDtoShallow(service));
        //Assert
        Assertions.assertThat(serviceDto).isNotNull();
        Assertions.assertThat(serviceDto.getId()).isNotNull();

    }

    @Test
    void updateService() {
        //Arrange
        List<ServiceEntity> services = sampleData.getNonEmptyListOfServiceEntity(2);
        ServiceDto beforeDto = serviceController.newService(EntityDtoMappers.toServiceDtoShallow(services.get(0)));
        UUID beforeId = beforeDto.getId();
        ServiceDto afterDto = serviceController.newService(EntityDtoMappers.toServiceDtoShallow(services.get(1)));
        UUID afterId = afterDto.getId();
        //Act
        serviceController.updateService(beforeId, afterDto);
        ServiceDto updated = serviceController.getService(beforeId);
        //Assert
        Assertions.assertThat(updated).isEqualTo(beforeDto);
    }

    @Test
    void removeDependencyFromService() {
        //Arrange
        List<ServiceEntity> services = sampleData.getNonEmptyListOfServiceEntity(3);
        ServiceEntity service = sampleData.getRandomizedServiceEntityWithNameNotInList(services);
        UUID serviceId = serviceRepository.save(service);
        service.setId(serviceId);

        for(ServiceEntity serv : services){
            serv.setId(serviceRepository.save(serv));
        }

        UUID dependency = services.get(0).getId();
        ServiceEntity removed = services.get(0);
        serviceRepository.addDependencyToService(service, services);
        Map.Entry<ServiceEntity, List<ServiceEntity>> before =
                serviceRepository.retrieveOneWithDependencies(serviceId);
        //Act
        serviceController.removeDependencyFromService(serviceId,dependency);
        Map.Entry<ServiceEntity, List<ServiceEntity>> after =
                serviceRepository.retrieveOneWithDependencies(serviceId);

        //Assert
        Assertions.assertThat(before.getValue().size()).isEqualTo(services.size());
        Assertions.assertThat(before.getValue()).contains(removed);
        Assertions.assertThat(after.getValue()).doesNotContain(removed);
    }

    @Test
    void deleteService() {
        //Arrange
        List<ServiceEntity> services = sampleData.getNonEmptyListOfServiceEntity(3);
        ServiceEntity service = sampleData.getRandomizedServiceEntityWithNameNotInList(services);
        UUID serviceId = serviceRepository.save(service);
        service.setId(serviceId);

        for(ServiceEntity serv : services){
            serv.setId(serviceRepository.save(serv));
        }

        serviceRepository.addDependencyToService(service, services);
        Map.Entry<ServiceEntity, List<ServiceEntity>> before =
                serviceRepository.retrieveOneWithDependencies(serviceId);
        //Act
        serviceController.deleteService(serviceId);
        Optional<ServiceEntity> shouldBeEmpty = serviceRepository.retrieve(serviceId);

        //Assert
        Assertions.assertThat(before.getValue().size()).isEqualTo(services.size());
        Assertions.assertThat(shouldBeEmpty).isEmpty();

    }

    @Test
    void getServicetypes() {
    }

    @Test
    void getServiceStatuses() {
    }
}