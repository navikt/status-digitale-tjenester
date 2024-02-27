package nav.statusplattform.core.enums;

import java.util.Optional;

public enum ServiceStatus implements DbEnum {
    OK("OK"),
    DOWN("DOWN"),
    ISSUE("ISSUE"),
    UNKNOWN("UNKNOWN");

    private String dbRepresentation;

    ServiceStatus(String dbRepresentation) {
        this.dbRepresentation = dbRepresentation;
    }

    @Override
    public String getDbRepresentation(){
        return dbRepresentation;
    }

    public static Optional<ServiceStatus> fromDb(String dbRepresentation){
        try{
            return Optional.of((ServiceStatus) DbEnum.findEnum(dbRepresentation, values(), ServiceStatus.class.getSimpleName()));
        }
        catch (IllegalArgumentException e){
            return Optional.empty();
        }
    }
}
