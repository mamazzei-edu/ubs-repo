package br.sp.gov.fatec.ubs.android.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.sp.gov.fatec.ubs.android.R
import br.sp.gov.fatec.ubs.android.data.api.RetrofitClient
import br.sp.gov.fatec.ubs.android.data.repository.AuthRepository
import br.sp.gov.fatec.ubs.android.databinding.ActivityMainBinding
import br.sp.gov.fatec.ubs.android.ui.login.LoginActivity
import br.sp.gov.fatec.ubs.android.ui.paciente.PacienteAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val adapter = PacienteAdapter(
        onItemClick = { paciente ->
            // Abrir tela de detalhes/edição
            val intent = Intent(this, br.sp.gov.fatec.ubs.android.ui.cadastro.CadastroPacienteActivity::class.java)
            intent.putExtra("PACIENTE_ID", paciente.id)
            intent.putExtra("MODO_EDICAO", true)
            startActivity(intent)
        },
        onDeleteClick = { paciente ->
            confirmarExclusao(paciente)
        }
    )
    private lateinit var authRepository: AuthRepository
    
    private var currentPage = 0
    private var totalPages = 1
    private var isLoading = false
    private val pageSize = 10 // Mostrar 10 pacientes por página
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            // Não usar setSupportActionBar para evitar conflito com tema
            // setSupportActionBar(binding.toolbar)
            title = "Pacientes UBS"
            
            authRepository = AuthRepository(this, RetrofitClient.apiService)
            
            setupRecyclerView()
            setupListeners()
            
            Toast.makeText(this, "✅ Bem-vindo ao Sistema UBS!", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Sempre recarregar lista quando voltar de outra tela
        loadPacientes()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    
    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            currentPage = 0
            loadPacientes()
        }
        
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, br.sp.gov.fatec.ubs.android.ui.cadastro.CadastroPacienteActivity::class.java)
            startActivity(intent)
        }
        
        // Busca por prontuário
        binding.etBusca.setOnEditorActionListener { _, _, _ ->
            val prontuario = binding.etBusca.text.toString().trim()
            if (prontuario.isNotEmpty()) {
                buscarPorProntuario(prontuario)
            } else {
                currentPage = 0
                loadPacientes()
            }
            true
        }
        
        // Botões de paginação
        binding.btnAnterior.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                loadPacientesPage(currentPage)
            }
        }
        
        binding.btnProxima.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                loadPacientesPage(currentPage)
            }
        }
    }
    
    private fun buscarPorProntuario(prontuario: String) {
        lifecycleScope.launch {
            try {
                binding.swipeRefresh.isRefreshing = true
                
                val response = RetrofitClient.apiService.getPacienteByProntuario(prontuario)
                
                if (response.isSuccessful && response.body() != null) {
                    val paciente = response.body()!!
                    adapter.submitList(listOf(paciente))
                    totalPages = 1
                    currentPage = 0
                    updatePaginationUI()
                    Toast.makeText(this@MainActivity, "✅ Paciente encontrado", Toast.LENGTH_SHORT).show()
                } else {
                    adapter.submitList(emptyList())
                    totalPages = 1
                    currentPage = 0
                    updatePaginationUI()
                    Toast.makeText(this@MainActivity, "❌ Paciente não encontrado", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
    
    private fun loadPacientes() {
        currentPage = 0
        loadPacientesPage(0)
    }
    
    private fun loadPacientesPage(page: Int) {
        lifecycleScope.launch {
            try {
                binding.swipeRefresh.isRefreshing = true
                isLoading = true
                
                val response = RetrofitClient.apiService.getPacientes(page, pageSize)
                
                if (response.isSuccessful && response.body() != null) {
                    val pageResponse = response.body()!!
                    adapter.submitList(pageResponse.content)
                    
                    totalPages = pageResponse.totalPages
                    currentPage = page
                    
                    // Atualizar UI de paginação
                    updatePaginationUI()
                    
                    // Rolar para o topo
                    binding.recyclerView.scrollToPosition(0)
                    
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                binding.swipeRefresh.isRefreshing = false
                isLoading = false
            }
        }
    }
    
    private fun updatePaginationUI() {
        binding.tvPaginacao.text = "Página ${currentPage + 1} de $totalPages"
        binding.btnAnterior.isEnabled = currentPage > 0
        binding.btnProxima.isEnabled = currentPage < totalPages - 1
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun confirmarExclusao(paciente: br.sp.gov.fatec.ubs.android.data.model.Paciente) {
        AlertDialog.Builder(this)
            .setTitle("Excluir paciente")
            .setMessage("Tem certeza que deseja excluir ${paciente.nomeCompleto}?\n\nEsta ação não poderá ser desfeita.")
            .setPositiveButton("Excluir") { _, _ ->
                excluirPaciente(paciente)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun excluirPaciente(paciente: br.sp.gov.fatec.ubs.android.data.model.Paciente) {
        lifecycleScope.launch {
            try {
                binding.swipeRefresh.isRefreshing = true
                
                val response = paciente.id?.let { 
                    RetrofitClient.apiService.deletePaciente(it) 
                }
                
                if (response != null && response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "✅ Paciente excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    
                    // Se era o último item da página e não é a primeira página, voltar uma página
                    if (adapter.itemCount == 1 && currentPage > 0) {
                        currentPage--
                    }
                    
                    // Recarregar a página atual
                    loadPacientesPage(currentPage)
                } else {
                    Toast.makeText(this@MainActivity, "❌ Erro ao excluir paciente", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "❌ Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
    
    private fun logout() {
        lifecycleScope.launch {
            authRepository.logout()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
