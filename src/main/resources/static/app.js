// tela da aplicacao, sem framework nenhum. consome /api/clientes, /api/cep, /api/fretes e /api/padroes

const $ = (seletor) => document.querySelector(seletor);
const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

/* ------------------------------ infraestrutura --------------------------- */

async function api(caminho, opcoes = {}) {
  const resposta = await fetch(caminho, {
    headers: { 'Content-Type': 'application/json' },
    ...opcoes
  });

  if (resposta.status === 204) return null;

  const texto = await resposta.text();
  const corpo = texto ? JSON.parse(texto) : null;

  if (!resposta.ok) {
    const erro = new Error((corpo && (corpo.detail || corpo.title)) || 'Falha na requisição');
    erro.erros = (corpo && corpo.erros) || [];
    throw erro;
  }
  return corpo;
}

function aviso(texto, tipo = 'ok', erros = []) {
  const caixa = document.createElement('div');
  caixa.className = `aviso ${tipo}`;
  caixa.innerHTML = `<strong>${escapar(texto)}</strong>` +
    (erros.length ? `<ul>${erros.map((e) => `<li>${escapar(e)}</li>`).join('')}</ul>` : '');
  $('#avisos').appendChild(caixa);
  setTimeout(() => caixa.remove(), erros.length ? 9000 : 4000);
}

function escapar(valor) {
  return String(valor ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;');
}

function dataHora(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'medium' });
}

function etiquetaFonte(fonte) {
  const classe = String(fonte || '').toLowerCase();
  return `<span class="etiqueta ${classe}">${escapar(fonte || '-')}</span>`;
}

function mascaraCep(valor) {
  const digitos = String(valor || '').replace(/[^0-9]/g, '').slice(0, 8);
  return digitos.length > 5 ? `${digitos.slice(0, 5)}-${digitos.slice(5)}` : digitos;
}

/* --------------------------------- abas ---------------------------------- */

document.querySelectorAll('nav.abas button').forEach((botao) => {
  botao.addEventListener('click', () => {
    document.querySelectorAll('nav.abas button').forEach((b) => b.setAttribute('aria-selected', 'false'));
    document.querySelectorAll('.painel').forEach((p) => p.classList.remove('ativo'));
    botao.setAttribute('aria-selected', 'true');
    $(`#painel-${botao.dataset.painel}`).classList.add('ativo');
    if (botao.dataset.painel === 'bastidores') carregarBastidores();
    if (botao.dataset.painel === 'padroes') carregarPadroes();
  });
});

/* --------------------------------- tema ---------------------------------- */

const btnTema = $('#btnTema');
const temaSalvo = localStorage.getItem('tema');
if (temaSalvo) document.documentElement.dataset.tema = temaSalvo;
atualizarRotuloTema();

btnTema.addEventListener('click', () => {
  const escuroAgora = document.documentElement.dataset.tema === 'escuro'
    || (!document.documentElement.dataset.tema && window.matchMedia('(prefers-color-scheme: dark)').matches);
  const novo = escuroAgora ? 'claro' : 'escuro';
  document.documentElement.dataset.tema = novo;
  localStorage.setItem('tema', novo);
  atualizarRotuloTema();
});

function atualizarRotuloTema() {
  const escuroAgora = document.documentElement.dataset.tema === 'escuro'
    || (!document.documentElement.dataset.tema && window.matchMedia('(prefers-color-scheme: dark)').matches);
  btnTema.textContent = escuroAgora ? 'Tema claro' : 'Tema escuro';
}

/* ------------------------------- clientes -------------------------------- */

const formCliente = $('#formCliente');
const campoId = $('#clienteId');

formCliente.addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const botao = $('#btnSalvar');
  botao.disabled = true;

  const corpo = {
    nome: $('#nome').value,
    email: $('#email').value,
    telefone: $('#telefone').value,
    cep: $('#cep').value
  };

  try {
    if (campoId.value) {
      await api(`/api/clientes/${campoId.value}`, { method: 'PUT', body: JSON.stringify(corpo) });
      aviso('Cliente atualizado. Um evento CLIENTE_ATUALIZADO foi publicado.');
    } else {
      await api('/api/clientes', { method: 'POST', body: JSON.stringify(corpo) });
      aviso('Cliente cadastrado! Veja a aba Bastidores para o evento e as notificações.');
    }
    limparFormulario();
    carregarClientes();
  } catch (erro) {
    aviso(erro.message, 'erro', erro.erros);
  } finally {
    botao.disabled = false;
  }
});

$('#btnCancelar').addEventListener('click', limparFormulario);

function limparFormulario() {
  formCliente.reset();
  campoId.value = '';
  $('#previaEndereco').textContent = '';
  $('#tituloFormulario').textContent = 'Novo cliente';
  $('#btnSalvar').textContent = 'Cadastrar cliente';
  $('#btnCancelar').hidden = true;
}

$('#cep').addEventListener('input', (e) => { e.target.value = mascaraCep(e.target.value); });
$('#freteCep').addEventListener('input', (e) => { e.target.value = mascaraCep(e.target.value); });
$('#cepConsulta').addEventListener('input', (e) => { e.target.value = mascaraCep(e.target.value); });

$('#cep').addEventListener('blur', async (evento) => {
  const cep = evento.target.value.replace(/[^0-9]/g, '');
  const previa = $('#previaEndereco');
  if (cep.length !== 8) {
    previa.textContent = '';
    return;
  }
  previa.textContent = 'Consultando endereço...';
  try {
    const endereco = await api(`/api/cep/${cep}`);
    previa.innerHTML = `${escapar([endereco.logradouro, endereco.bairro, endereco.localidade, endereco.uf]
      .filter(Boolean).join(', '))} ${etiquetaFonte(endereco.fonte)}`;
  } catch (erro) {
    previa.textContent = erro.message;
  }
});

let temporizadorFiltro;
$('#filtro').addEventListener('input', (evento) => {
  clearTimeout(temporizadorFiltro);
  temporizadorFiltro = setTimeout(() => carregarClientes(evento.target.value), 300);
});

async function carregarClientes(filtro = '') {
  try {
    const url = filtro ? `/api/clientes?nome=${encodeURIComponent(filtro)}` : '/api/clientes';
    const clientes = await api(url);
    const corpo = $('#tabelaClientes');
    $('#clientesVazio').hidden = clientes.length > 0;

    corpo.innerHTML = clientes.map((cliente) => {
      const endereco = cliente.endereco || {};
      const linhaEndereco = [endereco.logradouro, endereco.bairro].filter(Boolean).join(', ');
      const cidade = [endereco.localidade, endereco.uf].filter(Boolean).join('/');
      return `
        <tr>
          <td><strong>${escapar(cliente.nome)}</strong></td>
          <td>${escapar(cliente.email)}<br><span class="quando">${escapar(cliente.telefone || 'sem telefone')}</span></td>
          <td>${escapar(endereco.cep || '')}<br><span class="quando">${escapar(linhaEndereco)}${linhaEndereco ? ' · ' : ''}${escapar(cidade)}</span></td>
          <td>${etiquetaFonte(endereco.fonte)}</td>
          <td>
            <div class="acoes-linha">
              <button type="button" data-editar="${cliente.id}">Editar</button>
              <button type="button" data-remover="${cliente.id}">Remover</button>
            </div>
          </td>
        </tr>`;
    }).join('');

    corpo.querySelectorAll('[data-editar]').forEach((b) =>
      b.addEventListener('click', () => editarCliente(b.dataset.editar)));
    corpo.querySelectorAll('[data-remover]').forEach((b) =>
      b.addEventListener('click', () => removerCliente(b.dataset.remover)));
  } catch (erro) {
    aviso(erro.message, 'erro', erro.erros);
  }
}

async function editarCliente(id) {
  try {
    const cliente = await api(`/api/clientes/${id}`);
    campoId.value = cliente.id;
    $('#nome').value = cliente.nome;
    $('#email').value = cliente.email;
    $('#telefone').value = cliente.telefone || '';
    $('#cep').value = cliente.endereco ? cliente.endereco.cep : '';
    $('#tituloFormulario').textContent = `Editando ${cliente.nome}`;
    $('#btnSalvar').textContent = 'Salvar alterações';
    $('#btnCancelar').hidden = false;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  } catch (erro) {
    aviso(erro.message, 'erro', erro.erros);
  }
}

async function removerCliente(id) {
  if (!confirm('Remover este cliente?')) return;
  try {
    await api(`/api/clientes/${id}`, { method: 'DELETE' });
    aviso('Cliente removido. Evento CLIENTE_REMOVIDO publicado.');
    carregarClientes($('#filtro').value);
  } catch (erro) {
    aviso(erro.message, 'erro', erro.erros);
  }
}

/* --------------------------------- frete --------------------------------- */

$('#formFrete').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const corpo = {
    cep: $('#freteCep').value,
    pesoKg: Number($('#fretePeso').value),
    modalidade: $('#freteModalidade').value || null
  };

  try {
    const opcoes = await api('/api/fretes/simular', { method: 'POST', body: JSON.stringify(corpo) });
    const maisBarata = opcoes.find((o) => o.disponivel);

    $('#resultadoFrete').innerHTML = opcoes.map((opcao) => `
      <div class="opcao ${opcao.disponivel ? '' : 'indisponivel'} ${opcao === maisBarata ? 'melhor' : ''}">
        <strong>${escapar(opcao.rotulo)}</strong>
        <span class="valor">${opcao.disponivel ? moeda.format(opcao.valor) : 'Indisponível'}</span>
        <span class="prazo">${opcao.disponivel ? prazoEmTexto(opcao.prazoDias) : escapar(opcao.observacao)}</span>
        ${opcao.disponivel ? `<span class="prazo">${escapar(opcao.observacao || '')}</span>` : ''}
        <span class="classe">${escapar(opcao.estrategia)}</span>
      </div>`).join('');
  } catch (erro) {
    aviso(erro.message, 'erro', erro.erros);
  }
});

function prazoEmTexto(dias) {
  if (dias === 0) return 'Entrega no mesmo dia';
  return dias === 1 ? 'Em 1 dia útil' : `Em ${dias} dias úteis`;
}

async function carregarModalidades() {
  try {
    const modalidades = await api('/api/fretes/modalidades');
    const select = $('#freteModalidade');
    modalidades.forEach((m) => {
      const opcao = document.createElement('option');
      opcao.value = m.modalidade;
      opcao.textContent = `${m.rotulo} (${m.estrategia})`;
      select.appendChild(opcao);
    });
  } catch (erro) {
    aviso(erro.message, 'erro');
  }
}

/* ---------------------------------- CEP ---------------------------------- */

$('#formCep').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  try {
    const cep = $('#cepConsulta').value.replace(/[^0-9]/g, '');
    const endereco = await api(`/api/cep/${cep}`);
    $('#resultadoCep').innerHTML = `
      <div class="opcao" style="margin-top:16px">
        <strong>${escapar(endereco.cep)} ${etiquetaFonte(endereco.fonte)}</strong>
        <span>${escapar(endereco.logradouro || 'Logradouro não informado')}</span>
        <span class="prazo">${escapar([endereco.bairro, endereco.localidade, endereco.uf].filter(Boolean).join(' · '))}</span>
        <span class="classe">DDD ${escapar(endereco.ddd || '-')}</span>
      </div>`;
  } catch (erro) {
    $('#resultadoCep').innerHTML = '';
    aviso(erro.message, 'erro', erro.erros);
  }
});

/* -------------------------------- padrões -------------------------------- */

let padroesCarregados = false;

async function carregarPadroes() {
  if (padroesCarregados) return;
  try {
    const [padroes, cadeia] = await Promise.all([api('/api/padroes'), api('/api/padroes/chain')]);

    $('#listaPadroes').innerHTML = padroes.map((p) => `
      <article class="padrao">
        <span class="categoria">${escapar(p.categoria)}</span>
        <h3>${escapar(p.padrao)}</h3>
        <dl>
          <dt>Problema</dt><dd>${escapar(p.problema)}</dd>
          <dt>Solução aplicada</dt><dd>${escapar(p.solucao)}</dd>
          <dt>Como ver funcionando</dt><dd>${escapar(p.comoVerFuncionando)}</dd>
        </dl>
        <div class="classes">${p.classes.map((c) => `<code>${escapar(c)}</code>`).join('')}</div>
      </article>`).join('');

    $('#listaCadeia').innerHTML = cadeia.map((elo) =>
      `<li><strong>${escapar(elo.elo)}</strong>: ${escapar(elo.verifica)}</li>`).join('');

    padroesCarregados = true;
  } catch (erro) {
    aviso(erro.message, 'erro');
  }
}

/* ------------------------------ bastidores ------------------------------- */

async function carregarBastidores() {
  try {
    const [decorator, eventos, notificacoes, singleton] = await Promise.all([
      api('/api/padroes/decorator'),
      api('/api/padroes/eventos'),
      api('/api/padroes/notificacoes'),
      api('/api/padroes/singleton')
    ]);

    $('#decoratorImpl').textContent = decorator.implementacaoAtiva;
    const e = decorator.estatisticas;
    $('#metricasDecorator').innerHTML = `
      ${metrica(e.consultas, 'consultas')}
      ${metrica(e.acertosDeCache, 'servidas pelo cache')}
      ${metrica(e.chamadasExternas, 'chamadas ao ViaCEP')}
      ${metrica(e.quedasParaOffline, 'quedas para offline')}
      ${metrica(e.cepsEmCache, 'CEPs em cache')}`;

    $('#eventosVazio').hidden = eventos.length > 0;
    $('#listaEventos').innerHTML = eventos.map((evento) => `
      <li>
        <span class="etiqueta">${escapar(evento.tipo)}</span>
        <div>${escapar(evento.resumo)}</div>
        <span class="quando">${dataHora(evento.ocorridoEm)} · observado por ${escapar(evento.observador)}</span>
      </li>`).join('');

    $('#notificacoesVazio').hidden = notificacoes.length > 0;
    $('#listaNotificacoes').innerHTML = notificacoes.map((n) => `
      <li>
        <span class="etiqueta">${escapar(n.canal)}</span>
        <strong>${escapar(n.assunto)}</strong>
        <div class="quando">para ${escapar(n.destinatario)} · ${dataHora(n.enviadaEm)}</div>
        <pre class="mensagem">${escapar(n.corpo)}</pre>
      </li>`).join('');

    $('#singletonExplicacao').textContent = singleton.explicacao;
    $('#metricasSingleton').innerHTML = `
      <div class="metrica"><strong>${escapar(singleton.instanciaId)}</strong><span>id da instância</span></div>
      ${metrica(singleton.consultasAsTarifas, 'consultas às tarifas')}
      <div class="metrica"><strong>${singleton.mesmaInstancia ? 'sim' : 'não'}</strong><span>mesma instância nas duas buscas</span></div>`;
  } catch (erro) {
    aviso(erro.message, 'erro');
  }
}

function metrica(valor, rotulo) {
  return `<div class="metrica"><strong>${escapar(valor)}</strong><span>${escapar(rotulo)}</span></div>`;
}

$('#btnAtualizarBastidores').addEventListener('click', carregarBastidores);

$('#btnLimparHistorico').addEventListener('click', async () => {
  try {
    await api('/api/padroes/historico', { method: 'DELETE' });
    aviso('Histórico e cache zerados.');
    carregarBastidores();
  } catch (erro) {
    aviso(erro.message, 'erro');
  }
});

/* ------------------------------- inicialização --------------------------- */

carregarClientes();
carregarModalidades();
