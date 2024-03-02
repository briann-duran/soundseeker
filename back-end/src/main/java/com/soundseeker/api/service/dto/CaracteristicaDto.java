package com.soundseeker.api.service.dto;

import com.soundseeker.api.persistence.entity.CaracteristicaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CaracteristicaDto {
    private Long id;
    private String nombre;
    private String icono;

    public static CaracteristicaDto mapearDesde(CaracteristicaEntity caracteristica) {
        return new CaracteristicaDto(caracteristica.getId(), caracteristica.getNombre(), caracteristica.getIcono());
    }

    public static Set<CaracteristicaDto> mapearSet(Set<CaracteristicaEntity> caracteristicas) {
        return caracteristicas.stream().map(CaracteristicaDto::mapearDesde).collect(Collectors.toSet());
    }
}
