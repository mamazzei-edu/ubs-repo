import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Para usar ngIf, ngFor
import { FormsModule } from '@angular/forms'; // Para usar ngModel
 
@Component({
  selector: 'app-lista',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.css'] // Corrigido para styleUrls
})
export class ListaComponent {
  mensagem: string = ""; // Inicializando como string vazia
  termoBusca: string = ""; // Variável para capturar o texto da busca
  lista: Array<any> = [
    { codigo: 1, nome: 'João', documento: '123456789' },
    { codigo: 2, nome: 'Maria', documento: '987654321' },
    { codigo: 3, nome: 'Pedro', documento: '1122334455' }
  ]; // Exemplo de lista de dados
 
  constructor() {}
 
  // Função para pesquisar na lista
  pesquisar(): void {
    if (!this.termoBusca.trim()) {
      this.mensagem = 'Digite um termo para a pesquisa.';
      return;
    }
 
    // Filtrando a lista com base no termo de busca
    const resultado = this.lista.filter(item =>
      item.nome.toLowerCase().includes(this.termoBusca.toLowerCase()) ||
      item.documento.includes(this.termoBusca)
    );
 
    if (resultado.length === 0) {
      this.mensagem = 'Nenhum resultado encontrado.';
    } else {
      this.mensagem = '';
      this.lista = resultado; // Atualiza a lista com os resultados filtrados
    }
  }
 
  // Função de editar (para ser implementada)
  editar(item: any): void {
    console.log('Editar:', item);
    // Implementar lógica de edição, se necessário
  }
 
  // Função de remover
  remover(codigo: number): void {
    this.lista = this.lista.filter(item => item.codigo !== codigo);
    this.mensagem = 'Item removido com sucesso!';
  }
}
 
