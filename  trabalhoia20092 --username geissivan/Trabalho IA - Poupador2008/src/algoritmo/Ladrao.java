package algoritmo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.unifor.ia.heuristica.Heuristica;

import controle.Constantes;

public class Ladrao extends ProgramaLadrao {

	/** Posi��o de poupadores no campo de vis�o do agente ladr�o */
	private List<Integer> posicaoPoupador;

	/**
	 * Posi��o do rastro de um poupador no campo de vis�o do agente ladr�o
	 * posicaoRastroPoupador = -1 Nenhum Rastro encontrado 
	 * posicaoRastroPoupador >= 0 Rastro encontrado
	 */
	private Integer posicaoRastroPoupador;

	public int acao() {

		// Decisao que sera tomada pelo ladrao
		Integer decisao = 0;

		// Lista para armazenar as distancias manhattan
		List<Integer> manhttanBuffer = new ArrayList<Integer>();

		// Map que armazena as distancias manhattan e as possiveis decisoes
		Map<Integer, Integer> caminhosMap = new HashMap<Integer, Integer>();

		// Visao do Ladrao
		int[] visao = sensor.getVisaoIdentificacao();

		// Olfato do Ladrao
		int[] olfato = sensor.getAmbienteOlfatoPoupador();

		// Populando lista com a posicao dos poupadores
		verificarCampoVisaoLadrao(visao);

		// Se encontou algum poupador no campo de visao
		if (posicaoPoupador.size() > 0) {
			// Carrega o mapa 5 X 5 da visao do Ladrao
			Heuristica.carregaMapa();

			// Carrega os possiveis passos do Ladrao
			Heuristica.carregaPassosLadrao();
			List<Point> list = Heuristica.getPassosLadrao();

			// Percorre os possiveis passos do Ladrao, para escolher a melhor
			// decisao
			for (Point p : list) {
				// Recupera o index do Ponto p no Mapa de visao do Ladrao
				Integer indexPNoMapa = Heuristica.getIndexOfPointOnVisionMap(p);
				// Boolean para decidir se ele tem a possibilidade de seguir ou
				// nao
				boolean seguir = false;
				// Se os pontos forem P[3,4] ou P[4,3]
				if (p.getX() + p.getY() == 7) {
					seguir = Heuristica.sondarCaminho(visao[indexPNoMapa - 1]);
				} else {
					// Se forem os outros pontos
					seguir = Heuristica.sondarCaminho(visao[indexPNoMapa]);
				}
				
				// Se permitir seguir
				if (seguir) {
					// Inicia o Calculo da Heuristica

					Point pPoupador = new Point();

					// Recupera a posicao do poupador
					Integer posicao = posicaoPoupador.get(0);

					// Se for depois da posicao P[3,3] do Ladrao no mapa de
					// Visao do Ladrao
					if (posicao >= 12) {
						pPoupador = Heuristica
								.getPointFromVisionMap(posicao + 1);
					} else {
						pPoupador = Heuristica.getPointFromVisionMap(posicao);
					}
					// Calcula a distancia manhatam
					Integer manhattan = Heuristica.distanciaManhattan(p,
							pPoupador);
					// Armazena numa lista para depois pegar a de menor
					// distancia do objetivo
					manhttanBuffer.add(manhattan);
					// Armazena no hashmap a distancia manhattan e a possivel
					// decisao
					caminhosMap.put(manhattan, selecionarDirecaoLadrao(p));
				}
			}
			// Ordena por menor distancia manhattan
			Collections.sort(manhttanBuffer);
			// Seleciona a decisao pela menor distancia manhatam
			decisao = caminhosMap.get(manhttanBuffer.get(0));

			// retorna a decisao
			return decisao;
			
		} else if ((posicaoRastroPoupador = buscarRastroPoupadorCampoVisaoAgenteLadrao(olfato)) >= 0) { // Verifica o olfato do agente ladr�o

			return (int) (Math.random() * 5);			
			
		} else { // Anda aleatoriamente
			
			return (int) (Math.random() * 5);
			
		}
	}

	/*
	 * Mapeia posicao do poupador na visao do ladrao
	 */
	private void verificarCampoVisaoLadrao(int[] visao) {
		posicaoPoupador = new ArrayList<Integer>();
		for (int i = 0; i < visao.length; i++) {
			if (visao[i] == Constantes.numeroPoupador01
					|| visao[i] == Constantes.numeroPoupador02) {
				posicaoPoupador.add(i);
			}
		}
	}

	/**
	 * busca o rastro de agentes poupadores dentro do olfato do ladrao e retorna
	 * um valor determinando se encontrou o rastro:
	 * 
	 * return = -1 Nenhum Rastro encontrado 
	 * return >= 0 Rastro encontrado
	 * 
	 * @param olfato
	 *            List de inteiros do campo de olfato do agente ladr�o
	 * @return
	 */
	private Integer buscarRastroPoupadorCampoVisaoAgenteLadrao(int[] olfato) {

		Integer maiorProximidade = 0;
		Integer indice = -1;

		for (int i = 0; i < olfato.length; i++) {
			if (olfato[i] >= 1 && olfato[i] <= 5
					&& (olfato[i] > 0 && olfato[i] < maiorProximidade)) {
				maiorProximidade = olfato[i];
				indice = i;
			}
		}

		return indice;

	}

	/*
	 * Define a direcao que o ladrao ira tomar
	 */
	private Integer selecionarDirecaoLadrao(Point p) {
		Integer mov = 0;

		if (p.getX() == 2 && p.getY() == 3) {
			// Cima
			mov = 1;
		} else if (p.getX() == 4 && p.getY() == 3) {
			// Baixo
			mov = 2;
		} else if (p.getX() == 3 && p.getY() == 4) {
			// Direita
			mov = 3;
		} else if (p.getX() == 3 && p.getY() == 2) {
			// Esquerda
			mov = 4;
		}

		return mov;
	}
}