CREATE TABLE service_oh_group
(
    service_id      UUID        NOT NULL,
    group_id     UUID        NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp with time zone NULL,
    PRIMARY KEY (service_id, group_id),
    FOREIGN KEY (service_id) REFERENCES service (id),
    FOREIGN KEY (group_id) REFERENCES oh_group (id)
);