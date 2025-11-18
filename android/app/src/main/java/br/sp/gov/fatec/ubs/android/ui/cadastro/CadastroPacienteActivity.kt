package br.sp.gov.fatec.ubs.android.ui.cadastro

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.sp.gov.fatec.ubs.android.data.api.RetrofitClient
import br.sp.gov.fatec.ubs.android.data.model.Paciente
import br.sp.gov.fatec.ubs.android.databinding.ActivityCadastroPacienteBinding
import kotlinx.coroutines.launch

class CadastroPacienteActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCadastroPacienteBinding
    private var pacienteId: Long? = null
    private var modoEdicao = false
    private var pdfUri: Uri? = null
    
    // Launcher para selecionar PDF
    private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                pdfUri = uri
                binding.tvPdfSelecionado.text = "✅ PDF selecionado: ${uri.lastPathSegment}"
                binding.tvPdfSelecionado.visibility = View.VISIBLE
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Verificar se é modo edição
        pacienteId = intent.getLongExtra("PACIENTE_ID", -1).takeIf { it != -1L }
        modoEdicao = intent.getBooleanExtra("MODO_EDICAO", false)
        
        supportActionBar?.title = if (modoEdicao) "Editar Paciente" else "Cadastrar Paciente"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        if (modoEdicao && pacienteId != null) {
            carregarPaciente(pacienteId!!)
        }
        
        setupListeners()
    }
    
    private fun carregarPaciente(id: Long) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getPacienteById(id)
                if (response.isSuccessful && response.body() != null) {
                    val paciente = response.body()!!
                    preencherFormulario(paciente)
                }
            } catch (e: Exception) {
                Toast.makeText(this@CadastroPacienteActivity, "Erro ao carregar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun preencherFormulario(paciente: Paciente) {
        binding.etNomeCompleto.setText(paciente.nomeCompleto)
        binding.etProntuario.setText(paciente.prontuario)
        // Converter data de YYYY-MM-DD para DD/MM/AAAA
        val dataFormatada = paciente.dataNascimento?.let {
            val parts = it.split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else it
        }
        binding.etDataNascimento.setText(dataFormatada)
        binding.etCpf.setText(paciente.cpf ?: "")
        binding.etTelefone.setText(paciente.telefone ?: "")
        binding.etEmail.setText(paciente.email ?: "")
    }
    
    private fun setupListeners() {
        binding.btnUploadPdf.setOnClickListener {
            selecionarPdf()
        }
        
        binding.btnSalvar.setOnClickListener {
            salvarPaciente()
        }
    }
    
    private fun selecionarPdf() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pdfPickerLauncher.launch(Intent.createChooser(intent, "Selecionar PDF"))
    }
    
    private fun salvarPaciente() {
        // Validar campos obrigatórios
        val nome = binding.etNomeCompleto.text.toString().trim()
        val prontuario = binding.etProntuario.text.toString().trim()
        val dataNascimentoInput = binding.etDataNascimento.text.toString().trim()
        
        if (nome.isEmpty()) {
            binding.etNomeCompleto.error = "Nome é obrigatório"
            Toast.makeText(this, "❌ Preencha o nome completo", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (prontuario.isEmpty()) {
            binding.etProntuario.error = "Prontuário é obrigatório"
            Toast.makeText(this, "❌ Preencha o prontuário", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (dataNascimentoInput.isEmpty()) {
            binding.etDataNascimento.error = "Data de nascimento é obrigatória"
            Toast.makeText(this, "❌ Preencha a data de nascimento", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Se não for modo edição, verificar duplicidade
        if (!modoEdicao) {
            verificarDuplicidadeECadastrar(nome, prontuario, dataNascimentoInput)
        } else {
            processarSalvamento(nome, prontuario, dataNascimentoInput)
        }
    }
    
    private fun verificarDuplicidadeECadastrar(nome: String, prontuario: String, dataNascimentoInput: String) {
        lifecycleScope.launch {
            try {
                // Verificar por prontuário
                val responseProntuario = RetrofitClient.apiService.getPacienteByProntuario(prontuario)
                if (responseProntuario.isSuccessful && responseProntuario.body() != null) {
                    Toast.makeText(this@CadastroPacienteActivity, "❌ Já existe paciente com prontuário $prontuario", Toast.LENGTH_LONG).show()
                    return@launch
                }
                
                // Verificar por nome
                val responseNome = RetrofitClient.apiService.getPacienteByNome(nome)
                if (responseNome.isSuccessful && responseNome.body() != null) {
                    Toast.makeText(this@CadastroPacienteActivity, "❌ Já existe paciente com nome $nome", Toast.LENGTH_LONG).show()
                    return@launch
                }
                
                // Se não existe, prosseguir com cadastro
                processarSalvamento(nome, prontuario, dataNascimentoInput)
                
            } catch (e: Exception) {
                // Se der erro na verificação, prosseguir mesmo assim
                processarSalvamento(nome, prontuario, dataNascimentoInput)
            }
        }
    }
    
    private fun processarSalvamento(nome: String, prontuario: String, dataNascimentoInput: String) {
        
        // Converter data de DD/MM/AAAA para YYYY-MM-DD
        val dataNascimento = try {
            val parts = dataNascimentoInput.split("/")
            if (parts.size == 3) {
                "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}"
            } else {
                binding.etDataNascimento.error = "Formato inválido. Use DD/MM/AAAA"
                return
            }
        } catch (e: Exception) {
            binding.etDataNascimento.error = "Data inválida"
            return
        }
        
        // Criar objeto Paciente
        val paciente = Paciente(
            id = if (modoEdicao) pacienteId else null, // Garantir null no modo cadastro
            nomeCompleto = nome,
            prontuario = prontuario,
            dataNascimento = dataNascimento,
            cpf = binding.etCpf.text.toString().trim().takeIf { it.isNotEmpty() },
            telefone = binding.etTelefone.text.toString().trim().takeIf { it.isNotEmpty() },
            email = binding.etEmail.text.toString().trim().takeIf { it.isNotEmpty() }
        )
        
        // Salvar ou atualizar no backend
        lifecycleScope.launch {
            try {
                binding.btnSalvar.isEnabled = false
                binding.btnSalvar.text = if (modoEdicao) "Atualizando..." else "Salvando..."
                
                // Log para debug
                android.util.Log.d("CadastroPaciente", "Enviando: Nome=$nome, Prontuario=$prontuario, Data=$dataNascimento")
                
                val response = if (modoEdicao && pacienteId != null) {
                    RetrofitClient.apiService.updatePaciente(pacienteId!!, paciente)
                } else {
                    RetrofitClient.apiService.createPaciente(paciente)
                }
                
                if (response.isSuccessful) {
                    val mensagem = if (modoEdicao) "✅ Paciente atualizado com sucesso!" else "✅ Paciente cadastrado com sucesso!"
                    Toast.makeText(this@CadastroPacienteActivity, mensagem, Toast.LENGTH_SHORT).show()
                    finish() // Volta para a tela anterior
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                    android.util.Log.e("CadastroPaciente", "Erro ${response.code()}: $errorBody")
                    
                    val mensagemErro = when (response.code()) {
                        400 -> "❌ Dados inválidos: $errorBody"
                        409 -> "❌ Paciente já existe com este prontuário!"
                        500 -> "❌ Erro 500: Prontuário pode já existir ou dados inválidos"
                        else -> "❌ Erro ${response.code()}: $errorBody"
                    }
                    Toast.makeText(this@CadastroPacienteActivity, mensagemErro, Toast.LENGTH_LONG).show()
                }
                
            } catch (e: Exception) {
                Toast.makeText(this@CadastroPacienteActivity, "❌ Erro: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } finally {
                binding.btnSalvar.isEnabled = true
                binding.btnSalvar.text = "Salvar"
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
