USE [ControlRFIDCapacitacion]
GO
/****** Object:  StoredProcedure [dbo].[Proc_Usuarios] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Autor: Isela Cruz
-- Fecha de Creación: 02/09/2026
-- Description: SP Usuarios
-- Modificación: se agrega @Usuario para poder filtrar por nombre de
--               usuario en el login (Accion=1), sin traer todo el catálogo.
-- =============================================

ALTER   PROCEDURE [dbo].[Proc_Usuarios]
    @XML Text = '',
    @IdUsuario Int = 0,
    @Usuario VarChar(20) = '',
    @Accion int = 1
As
BEGIN

--EjemploXML--
/*
declare @xml varchar(MAX)
SET @xml  =  '
<?xml version = "1.0" encoding = "iso-8859-1"?>
<ROOT>
    <Usuarios>
        <IdUsuario>1</IdUsuario>
        <Nombre>Usuario Prueba</Nombre>
        <Usuario>admin</Usuario>
        <Password>1234</Password>
        <FechaAlta>2026-09-02</FechaAlta>
        <FechaMod>2026-09-02</FechaMod>
        <UsuarioAlta>SYSTEM</UsuarioAlta>
        <UsuarioMod>SYSTEM</UsuarioMod>
        <Activo>1</Activo>
    </Usuarios>
</ROOT>'*/
--Termina Ejemplo XML

    /*Consulta*/
    IF (@Accion = 1)
    BEGIN
        SELECT *
        FROM Usuarios
        WHERE (@IdUsuario = 0 OR IdUsuario = @IdUsuario)
          AND (@Usuario = '' OR Usuario = @Usuario)
    END

    --Validamos que la variable XML contenga un valor
    IF NOT (@XML LIKE '')
    BEGIN
        DECLARE @doc int

        --Abrimos el xml
        EXEC sp_xml_preparedocument @doc OUTPUT, @XML

        --Guardamos los datos XML en una tabla temporal
        SELECT *
        INTO #Usuarios
        FROM OpenXml (@doc, '/ROOT/Usuarios', 2)
        WITH
        (
            IdUsuario Int,
            Nombre VarChar(150),
            Usuario VarChar(20),
            Password VarChar(200),
            FechaAlta DateTime,
            FechaMod DateTime,
            UsuarioAlta VarChar(20),
            UsuarioMod VarChar(20),
            Activo Bit
        )

        EXEC sp_xml_removedocument @doc

        /*Agregar*/
        IF (@Accion = 2)
        BEGIN
            INSERT INTO Usuarios
            (
                Nombre,
                Usuario,
                Password,
                FechaAlta,
                FechaMod,
                UsuarioAlta,
                UsuarioMod,
                Activo
            )
            SELECT
                Nombre,
                Usuario,
                Password,
                FechaAlta,
                FechaMod,
                UsuarioAlta,
                UsuarioMod,
                Activo
            FROM #Usuarios

            SELECT *
            FROM Usuarios
            WHERE IdUsuario = SCOPE_IDENTITY()
        END

        /*Modificar*/
        IF (@Accion = 3)
        BEGIN
            UPDATE Usuarios SET
                Usuarios.Nombre = #Usuarios.Nombre,
                Usuarios.Usuario = #Usuarios.Usuario,
                Usuarios.Password = #Usuarios.Password,
                Usuarios.FechaMod = #Usuarios.FechaMod,
                Usuarios.UsuarioMod = #Usuarios.UsuarioMod,
                Usuarios.Activo = #Usuarios.Activo
            FROM #Usuarios
            WHERE Usuarios.IdUsuario = #Usuarios.IdUsuario

            SELECT *
            FROM Usuarios
            WHERE IdUsuario = (SELECT IdUsuario FROM #Usuarios)
        END

        DROP TABLE #Usuarios
    END
END
