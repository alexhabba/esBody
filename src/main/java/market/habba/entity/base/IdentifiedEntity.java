package market.habba.entity.base;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Базовая сущность с генерируемым идентификатором.
 */
@MappedSuperclass
@Getter
@Setter
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class IdentifiedEntity {

    /**
     * Идентификатор объекта.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
}
