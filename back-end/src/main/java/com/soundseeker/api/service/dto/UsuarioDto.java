package com.soundseeker.api.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soundseeker.api.persistence.validation.ValidEmail;
import com.soundseeker.api.persistence.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    @NotBlank(message = "El nombre de usuario no puede ser nulo o estar vacío.")
    @Pattern(regexp = "^(?!.*([\\W_]).*\\1)[\\w.-]{4,20}$", message = "El nombre de usuario solo puede contener " +
            "letras minúsculas y mayúsculas sin acentos (a-z o A-Z), números (0-9), guion (-), guion bajo (_) y " +
            "punto (.); ningún caracter especial puede estar seguido de otro.")
    @Size(min = 4, max = 20, message = "El nombre de usuario debe tener mínimo 4 caracteres y máximo 20.")
    private String nombreUsuario;

    @NotBlank(message = "La contraseña no puede ser nula o estar vacía.")
    @Size(min = 8, max = 60, message = "La contraseña debe tener mínimo 8 caracteres.")
    @ValidPassword(message = "La contraseña no cumple con los requisitos de seguridad, esta debe contener mínimo " +
            "un dígito, una letra mayúscula, una letra minúscula y un caracter especial.")
    private String contrasena;

    @NotBlank(message = "La contraseña repetida no puede ser nula o estar vacía.")
    @Size(min = 8, max = 60, message = "La contraseña repetida debe tener mínimo 8 caracteres.")
    private String contrasenaConfirmada;

    @NotBlank(message = "El nombre no puede ser nulo o estar vacío.")
    @Size(min = 2, max = 30, message = "El nombre debe tener mínimo 2 caracteres y máximo 30.")
    @Pattern(regexp = "^[\\p{IsLatin}' -]+$", flags = Pattern.Flag.UNICODE_CASE, message = "Tu nombre solamente " +
            "puede contener caracteres latinos, apóstrofo ('), guion (-) y espacios.")
    private String nombre;

    @NotBlank(message = "El apellido no puede ser nulo o estar vacío.")
    @Size(min = 2, max = 30, message = "El apellido debe tener mínimo 2 caracteres y máximo 30.")
    @Pattern(regexp = "^[\\p{IsLatin}' -]+$", flags = Pattern.Flag.UNICODE_CASE, message = "Tu apellido solamente " +
            "puede contener caracteres latinos, apóstrofo ('), guion (-) y espacios.")
    private String apellido;

    @NotBlank(message = "El correo electrónico no puede ser nulo o estar vacío.")
    @ValidEmail(message = "El correo electrónico no es válido.")
    private String correoElectronico;
}