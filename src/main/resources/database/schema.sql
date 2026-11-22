create table if not exists EMPRESA (
    EmpresaId     INTEGER PRIMARY KEY AUTOINCREMENT,
    Nombre        VARCHAR(100) not null,
    Sector        VARCHAR(60),
    Web           VARCHAR(150),
    Ubicacion     VARCHAR(100),
    LinkedIn      VARCHAR(150),
    Notas         VARCHAR(255),
    FechaRegistro DATE not null,
    Active        BOOLEAN not null default 1
);

create table if not exists OFERTA (
    OfertaId         INTEGER PRIMARY KEY AUTOINCREMENT,
    EmpresaId        INTEGER not null,
    Titulo           VARCHAR(100) not null,
    Descripcion      VARCHAR(255),
    Estado           VARCHAR(20) not null default 'PENDIENTE',
    FechaPostulacion DATE not null,
    Enlace           VARCHAR(200),
    Salario          DECIMAL(10,2),
    Active           BOOLEAN not null default 1,
    CONSTRAINT FK_Oferta_Empresa FOREIGN KEY (EmpresaId) REFERENCES EMPRESA(EmpresaId)
);

create table if not exists ENTREVISTA (
    EntrevistaId INTEGER PRIMARY KEY AUTOINCREMENT,
    OfertaId     INTEGER not null,
    Fecha        DATE not null,
    Tipo         VARCHAR(30) not null,
    Notas        VARCHAR(255),
    Resultado    VARCHAR(30),
    Active       BOOLEAN not null default 1,
    CONSTRAINT FK_Entrevista_Oferta FOREIGN KEY (OfertaId) REFERENCES OFERTA(OfertaId)
);

create table if not exists NOTA (
    NotaId   INTEGER PRIMARY KEY AUTOINCREMENT,
    OfertaId INTEGER not null,
    Fecha    DATE not null,
    Texto    VARCHAR(255) not null,
    Active   BOOLEAN not null default 1,
    CONSTRAINT FK_Nota_Oferta FOREIGN KEY (OfertaId) REFERENCES OFERTA(OfertaId)
);

create table if not exists ACTIVIDAD (
    ActividadId INTEGER PRIMARY KEY AUTOINCREMENT,
    OfertaId    INTEGER,
    Tipo        VARCHAR(20) not null,
    Descripcion VARCHAR(255) not null,
    Fecha       DATETIME not null,
    Active      BOOLEAN not null default 1,
    CONSTRAINT FK_Actividad_Oferta FOREIGN KEY (OfertaId) REFERENCES OFERTA(OfertaId)
);
