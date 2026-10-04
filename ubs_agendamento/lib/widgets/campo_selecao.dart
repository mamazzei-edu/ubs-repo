import 'package:flutter/material.dart';

import '../tema.dart';

/// Campo de formulário que abre uma lista de opções numa folha inferior.
///
/// Usado no lugar do DropdownButtonFormField, cuja API mudou entre versões
/// recentes do Flutter (value -> initialValue), e porque listas longas de
/// médicos ficam melhores numa folha rolável no celular.
class CampoSelecao<T> extends StatelessWidget {
  const CampoSelecao({
    super.key,
    required this.rotulo,
    required this.opcoes,
    required this.textoDaOpcao,
    required this.aoSelecionar,
    this.selecionado,
    this.subtituloDaOpcao,
    this.icone,
    this.ajuda,
    this.mensagemSemOpcoes = 'Nenhuma opção disponível.',
    this.validator,
    this.habilitado = true,
  });

  final String rotulo;
  final List<T> opcoes;
  final T? selecionado;
  final String Function(T) textoDaOpcao;
  final String? Function(T)? subtituloDaOpcao;
  final ValueChanged<T> aoSelecionar;
  final IconData? icone;
  final String? ajuda;
  final String mensagemSemOpcoes;
  final String? Function(T?)? validator;
  final bool habilitado;

  Future<void> _abrir(BuildContext context) async {
    final escolhido = await showModalBottomSheet<T>(
      context: context,
      isScrollControlled: true,
      showDragHandle: true,
      builder: (context) => _ListaOpcoes<T>(
        titulo: rotulo,
        opcoes: opcoes,
        selecionado: selecionado,
        textoDaOpcao: textoDaOpcao,
        subtituloDaOpcao: subtituloDaOpcao,
        mensagemSemOpcoes: mensagemSemOpcoes,
      ),
    );
    if (escolhido != null) {
      aoSelecionar(escolhido);
    }
  }

  @override
  Widget build(BuildContext context) {
    return FormField<T>(
      // A chave muda com a seleção para o FormField recriar o estado e o
      // validator enxergar o valor atual.
      key: ValueKey(selecionado),
      initialValue: selecionado,
      validator: validator,
      builder: (estado) => InkWell(
        onTap: habilitado ? () => _abrir(context) : null,
        borderRadius: BorderRadius.circular(4),
        child: InputDecorator(
          isEmpty: selecionado == null,
          decoration: decoracaoCampo(
            rotulo,
            icone: icone,
            ajuda: ajuda,
            sufixo: const Icon(Icons.arrow_drop_down),
          ).copyWith(errorText: estado.errorText, enabled: habilitado),
          child: selecionado == null
              ? null
              : Text(textoDaOpcao(selecionado as T), maxLines: 1, overflow: TextOverflow.ellipsis),
        ),
      ),
    );
  }
}

/// Campo de formulário que executa uma ação ao ser tocado (abrir o seletor de
/// data ou de hora) e mostra o valor já formatado.
class CampoAcao extends StatelessWidget {
  const CampoAcao({
    super.key,
    required this.rotulo,
    required this.valor,
    required this.aoTocar,
    this.icone,
    this.validator,
  });

  final String rotulo;
  final String? valor;
  final VoidCallback aoTocar;
  final IconData? icone;
  final String? Function(String?)? validator;

  @override
  Widget build(BuildContext context) {
    return FormField<String>(
      key: ValueKey(valor),
      initialValue: valor,
      validator: validator,
      builder: (estado) => InkWell(
        onTap: aoTocar,
        borderRadius: BorderRadius.circular(4),
        child: InputDecorator(
          isEmpty: valor == null,
          decoration: decoracaoCampo(rotulo, icone: icone).copyWith(errorText: estado.errorText),
          child: valor == null ? null : Text(valor!),
        ),
      ),
    );
  }
}

class _ListaOpcoes<T> extends StatelessWidget {
  const _ListaOpcoes({
    required this.titulo,
    required this.opcoes,
    required this.selecionado,
    required this.textoDaOpcao,
    required this.subtituloDaOpcao,
    required this.mensagemSemOpcoes,
  });

  final String titulo;
  final List<T> opcoes;
  final T? selecionado;
  final String Function(T) textoDaOpcao;
  final String? Function(T)? subtituloDaOpcao;
  final String mensagemSemOpcoes;

  @override
  Widget build(BuildContext context) {
    final altura = MediaQuery.of(context).size.height * 0.7;
    return SafeArea(
      child: ConstrainedBox(
        constraints: BoxConstraints(maxHeight: altura),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(24, 0, 24, 8),
              child: Text(titulo, style: Theme.of(context).textTheme.titleLarge),
            ),
            if (opcoes.isEmpty)
              Padding(
                padding: const EdgeInsets.all(24),
                child: Text(mensagemSemOpcoes, textAlign: TextAlign.center),
              )
            else
              Flexible(
                child: ListView.builder(
                  shrinkWrap: true,
                  itemCount: opcoes.length,
                  itemBuilder: (context, indice) {
                    final opcao = opcoes[indice];
                    final subtitulo = subtituloDaOpcao?.call(opcao);
                    return ListTile(
                      title: Text(textoDaOpcao(opcao)),
                      subtitle: subtitulo == null ? null : Text(subtitulo),
                      trailing: opcao == selecionado ? const Icon(Icons.check) : null,
                      onTap: () => Navigator.of(context).pop(opcao),
                    );
                  },
                ),
              ),
          ],
        ),
      ),
    );
  }
}
