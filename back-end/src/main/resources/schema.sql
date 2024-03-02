# ROL
create table soundseeker.rol
(
    id  bigint auto_increment
        primary key,
    rol varchar(30) not null,
    constraint UK_gidd9huji2j14xop37v9dc7li
        unique (rol)
);

# USUARIO
create table soundseeker.usuario
(
    nombre_usuario     varchar(20) not null
        primary key,
    contrasena         varchar(60) not null,
    nombre             varchar(30) not null,
    apellido           varchar(30) not null,
    correo_electronico varchar(50) not null,
    bloqueado          tinyint     not null,
    deshabilitado      tinyint     not null
);

# ROLES_USUARIOS
create table soundseeker.roles_usuarios
(
    nombre_usuario varchar(20) not null,
    rol_usuario    bigint      not null,
    constraint FK43kjwep0a9k50rj8g3669wj1m
        foreign key (rol_usuario) references soundseeker.rol (id),
    constraint FKddnabtc3xyekgh9kuqhm6nsk7
        foreign key (nombre_usuario) references soundseeker.usuario (nombre_usuario)
);

# TOKEN_VERIFICACION
create table soundseeker.token_verificacion
(
    id               bigint auto_increment
        primary key,
    token            varchar(255) null,
    fecha_expiracion datetime     not null,
    nombre_usuario   varchar(20)  not null,
    constraint UK_kgcfvr4xwkt7mmccwjt6owo4
        unique (nombre_usuario),
    constraint FKj54q2r7rjkl431qgletakuwhb
        foreign key (nombre_usuario) references soundseeker.usuario (nombre_usuario)
);

# POLITICA
create table soundseeker.politica
(
    id          bigint auto_increment
        primary key,
    titulo      varchar(70)   not null,
    descripcion varchar(500)  not null,
    imagen      varchar(1000) null
);

# CATEGORIA
create table soundseeker.categoria
(
    id          bigint auto_increment
        primary key,
    nombre      varchar(30)  not null,
    imagen      varchar(255) not null,
    disponible  tinyint(1)   null,
    descripcion varchar(500) null,
    constraint UK_35t4wyxqrevf09uwx9e9p6o75
        unique (nombre)
);

# POLITICA_CATEGORIA
create table soundseeker.politica_categoria
(
    politica_id  bigint not null,
    categoria_id bigint not null,
    primary key (politica_id, categoria_id),
    constraint FK_politica_categoria_categoria
        foreign key (categoria_id) references soundseeker.categoria (id),
    constraint FK_politica_categoria_politica
        foreign key (politica_id) references soundseeker.politica (id)
);

# CARACTERISTICA
create table soundseeker.caracteristica
(
    id     bigint auto_increment
        primary key,
    nombre varchar(60) not null,
    icono  varchar(60) null,
    constraint UK_a33pqwtvbck2lvjojp8s48k32
        unique (nombre)
);

# PRODUCTO
create table soundseeker.producto
(
    id           bigint auto_increment
        primary key,
    nombre       varchar(60)    not null,
    descripcion  varchar(1000)  not null,
    marca        varchar(60)    not null,
    precio       decimal(10, 2) not null,
    disponible   tinyint(1)     not null,
    categoria_id bigint         not null,
    constraint UK_9su14n91mtgcg5ehl658v4afx
        unique (nombre),
    constraint FKodqr7965ok9rwquj1utiamt0m
        foreign key (categoria_id) references soundseeker.categoria (id)
);

# IMAGENES_PRODUCTO
create table soundseeker.imagenes_producto
(
    producto_id bigint        not null,
    imagen      varchar(1000) null,
    constraint FKlr6na82038ee5ivfffti2riea
        foreign key (producto_id) references soundseeker.producto (id)
);

# PRODUCTO_CARACTERISTICA
create table soundseeker.producto_caracteristica
(
    producto_id       bigint not null,
    caracteristica_id bigint not null,
    primary key (producto_id, caracteristica_id),
    constraint FKnscvtbxrba4cw9xeop9xsw4et
        foreign key (producto_id) references soundseeker.producto (id),
    constraint FKnvjtxia0m1lkhwxkinsyll6ew
        foreign key (caracteristica_id) references soundseeker.caracteristica (id)
);

# FAVORITO
create table soundseeker.favorito
(
    producto_id            bigint      not null,
    usuario_nombre_usuario varchar(20) not null,
    primary key (producto_id, usuario_nombre_usuario),
    constraint FKam2d6jb3e39532amsnb3m65wk
        foreign key (producto_id) references soundseeker.producto (id),
    constraint FKm1e3aew8d7egbutbyrye17a3q
        foreign key (usuario_nombre_usuario) references soundseeker.usuario (nombre_usuario)
);

# RESERVA
create table soundseeker.reserva
(
    id                     bigint auto_increment
        primary key,
    fecha_orden            datetime     not null,
    fecha_retiro           date         not null,
    fecha_entrega          date         not null,
    notas                  varchar(500) null,
    calificacion           int          null,
    usuario_nombre_usuario varchar(20)  not null,
    constraint FK9qs7ftkg22p6gbachchhysofr
        foreign key (usuario_nombre_usuario) references soundseeker.usuario (nombre_usuario),
    check ((`calificacion` >= 1) and (`calificacion` <= 5))
);

# PRODUCTO_RESERVA
create table soundseeker.producto_reserva
(
    producto_id bigint not null,
    reserva_id  bigint not null,
    primary key (producto_id, reserva_id),
    constraint FK_producto_reserva_producto
        foreign key (producto_id) references soundseeker.producto (id),
    constraint FK_producto_reserva_reserva
        foreign key (reserva_id) references soundseeker.reserva (id)
);