package br.com.fiap.postech.tabletrek.helper;

import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.entities.Usuario;
import br.com.fiap.postech.tabletrek.security.JwtService;

import java.util.UUID;

public class UsuarioHelper {

    public static Usuario getUsuario(Boolean geraId) {
        var usuario = new Usuario(
                "Daiane Gonçalves",
                "ddddd"+ (10 + (int) (Math.random() * 10000) ) +"@gmail.com",
                "654321",
                119912341234L);
        if (!geraId) {
            usuario.setId(null);
        }
        return usuario;
    }
    public static UsuarioDTO getUsuarioDTO(Boolean geraId) {
        var usuario = getUsuario(geraId);
        return getUsuarioDTO(usuario);
    }
    public static UsuarioDTO getUsuarioDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getTelefone()
        );
    }

    public static String getToken() {
        return getToken("anderson.wagner@gmail.com");
        //return "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmRlcnNvbi53YWduZXJAZ21haWwuY29tIiwiaWF0IjoxNzEwMTA4MjA0LCJleHAiOjE3MTAxMTE4MDR9.16xG239CfhXXNznMJ8dVsARbjYr6ZDV1b4N01bSpp08";
    }

    public static String getToken(String email) {
        return "Bearer " + new JwtService().generateToken(email);
    }
}
