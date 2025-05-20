package com.es.body.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    private Long id;

    private String phone;


    private String fullName;

    private String city;

    private String nameAdder;

    private boolean isSend;

    @CreationTimestamp
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Qr> qrc;

    @Override
    public String toString() {
        return
                "телефон : " + phone + "\n" +
                        "имя клиента : " + fullName;
    }

}
