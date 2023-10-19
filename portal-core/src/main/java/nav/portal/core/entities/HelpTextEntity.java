package nav.portal.core.entities;

import nav.portal.core.enums.ServiceType;

import java.util.Objects;

public class HelpTextEntity {
    private long nr; // nr represents number
    private ServiceType type;
    private String content;

    public long getNr() {
        return nr;
    }

    public HelpTextEntity setNr(long nr) {
        this.nr = nr;
        return this;
    }

    public ServiceType getType() {
        return type;
    }

    public HelpTextEntity setType(ServiceType type) {
        this.type = type;
        return this;
    }

    public String getContent() {
        return content;
    }

    public HelpTextEntity setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HelpTextEntity)) return false;
        HelpTextEntity that = (HelpTextEntity) o;
        return getNr() == that.getNr() && getType() == that.getType() && Objects.equals(getContent(), that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNr(), getType(), getContent());
    }
}
