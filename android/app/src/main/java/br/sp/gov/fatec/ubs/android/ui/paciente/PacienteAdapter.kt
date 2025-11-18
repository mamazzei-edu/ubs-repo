package br.sp.gov.fatec.ubs.android.ui.paciente

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.sp.gov.fatec.ubs.android.data.model.Paciente
import br.sp.gov.fatec.ubs.android.databinding.ItemPacienteBinding

class PacienteAdapter(
    private val onItemClick: (Paciente) -> Unit = {},
    private val onDeleteClick: (Paciente) -> Unit = {}
) : ListAdapter<Paciente, PacienteAdapter.PacienteViewHolder>(PacienteDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val binding = ItemPacienteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PacienteViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = getItem(position)
        holder.bind(paciente, onItemClick, onDeleteClick)
    }
    
    class PacienteViewHolder(private val binding: ItemPacienteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            paciente: Paciente,
            onItemClick: (Paciente) -> Unit,
            onDeleteClick: (Paciente) -> Unit
        ) {
            binding.apply {
                tvNome.text = paciente.nomeCompleto
                tvProntuario.text = "Prontuário: ${paciente.prontuario}"
                tvDataNascimento.text = "Nascimento: ${paciente.dataNascimento ?: "-"}"
                tvCpf.text = "CPF: ${paciente.cpf ?: "-"}"
                root.setOnClickListener { onItemClick(paciente) }
                btnDelete.setOnClickListener { onDeleteClick(paciente) }
            }
        }
    }
    
    class PacienteDiffCallback : DiffUtil.ItemCallback<Paciente>() {
        override fun areItemsTheSame(oldItem: Paciente, newItem: Paciente): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Paciente, newItem: Paciente): Boolean {
            return oldItem == newItem
        }
    }
}
