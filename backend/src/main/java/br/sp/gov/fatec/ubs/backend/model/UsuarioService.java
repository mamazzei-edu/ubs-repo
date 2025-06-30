package br.sp.gov.fatec.ubs.backend.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios()
    {
        return usuarioRepository.findAll();  // Busca todos os pacientes no banco
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);  // Busca o paciente pelo ID
    }

    public Usuario salvarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);  // Salva o paciente no banco
    }

    public void excluirUsuario(Long id) {
        usuarioRepository.deleteById(id);  // Exclui o paciente pelo ID
    }
}
