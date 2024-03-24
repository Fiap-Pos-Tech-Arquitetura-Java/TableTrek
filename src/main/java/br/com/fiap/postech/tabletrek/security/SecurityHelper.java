package br.com.fiap.postech.tabletrek.security;

import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityHelper {
    private final UsuarioService usuarioService;
    @Autowired
    public SecurityHelper(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public UsuarioDTO getUsuarioLogado() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioService.findByEmail(userDetails.getUsername());
    }
}
