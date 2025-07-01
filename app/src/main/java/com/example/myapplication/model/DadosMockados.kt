package com.example.myapplication.model

import com.example.myapplication.R
import kotlinx.serialization.Serializable
import java.util.UUID


data class Receita(
    val id: Int,
    val nome: String,
    val descricaoCurta: String,
    val imagemUrl: Any,
    val ingredientes: List<String>,
    val modoPreparo: List<String>,
    val tempoPreparo: String,
    val porcoes: Int,
    var isFavorita: Boolean = false
)

data class FAQItem(
    val pergunta: String,
    val resposta: String
)

@Serializable
data class ListaComprasItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    var isBought: Boolean = false
)

object DadosMockados {
    val listaDeReceitas = listOf(
        Receita(
            id = 1,
            nome = "Salada de Quinoa com Vegetais",
            descricaoCurta = "Uma salada nutritiva e refrescante.",
            imagemUrl = R.drawable.salada_de_quinoa_com_vegetais,
            ingredientes = listOf(
                "1 xícara de quinoa cozida",
                "1 pepino picado",
                "1 tomate picado",
                "1/4 cebola roxa picada (opcional)",
                "1/4 pimentão verde picado (opcional)",
                "azeite, limão e sal a gosto",
                "pimenta do reino a gosto",
                "folhas de hortelã ou coentro fresco (opcional)"
            ),
            modoPreparo = listOf(
                "Cozinhe a quinoa conforme as instruções da embalagem e deixe esfriar.",
                "Pique todos os vegetais (pepino, tomate, cebola roxa, pimentão) em cubos pequenos.",
                "Em uma tigela grande, misture a quinoa já fria com os vegetais picados.",
                "Prepare o molho misturando azeite, suco de limão, sal e pimenta a gosto.",
                "Despeje o molho sobre a salada e misture bem para incorporar todos os sabores.",
                "Sirva gelada e, se desejar, adicione folhas frescas de hortelã ou coentro picado."
            ),
            tempoPreparo = "20 min",
            porcoes = 2
        ),
        Receita(
            id = 2,
            nome = "Frango Assado com Batata Doce",
            descricaoCurta = "Um prato clássico e saudável.",
            imagemUrl = R.drawable.frango_assado_com_batata_doce,
            ingredientes = listOf(
                "2 filés de frango (aproximadamente 150-200g cada)",
                "2 batatas doces médias",
                "2 colheres de sopa de azeite de oliva",
                "1 ramo de alecrim fresco (ou 1 colher de chá de alecrim seco)",
                "sal a gosto",
                "pimenta do reino a gosto"
            ),
            modoPreparo = listOf(
                "Pré-aqueça o forno a 200°C.",
                "Tempere os filés de frango com sal, pimenta-do-reino e um fio de azeite.",
                "Descasque e corte as batatas doces em cubos ou rodelas.",
                "Em uma assadeira, disponha o frango e as batatas doces. Regue as batatas com azeite e polvilhe alecrim fresco ou seco.",
                "Leve ao forno por cerca de 30 a 40 minutos, ou até o frango estar cozido por completo e as batatas macias e levemente douradas."
            ),
            tempoPreparo = "50 min",
            porcoes = 2
        ),
        Receita(
            id = 3,
            nome = "Smoothie de Frutas Vermelhas",
            descricaoCurta = "Delicioso e cheio de antioxidantes.",
            imagemUrl = R.drawable.smoothie_de_frutas_vermelhas,
            ingredientes = listOf(
                "1 xícara de frutas vermelhas congeladas (morango, mirtilo, framboesa)",
                "1/2 banana média (fresca ou congelada)",
                "1/2 xícara de iogurte natural (ou leite vegetal)",
                "1 colher de chá de mel ou agave (opcional)"
            ),
            modoPreparo = listOf(
                "Coloque todas as frutas vermelhas congeladas, a banana, o iogurte natural (ou leite vegetal) e o mel (ou agave, se for usar) no liquidificador.",
                "Bata em velocidade alta até obter uma mistura homogênea e cremosa.",
                "Sirva imediatamente em um copo alto."
            ),
            tempoPreparo = "5 min",
            porcoes = 1
        ),
        Receita(
            id = 4,
            nome = "Lasanha de Abobrinha sem Massa",
            descricaoCurta = "Uma versão leve e saborosa da lasanha tradicional.",
            imagemUrl = R.drawable.lasanha_de_abobrinha_sem_massa,
            ingredientes = listOf("2 abobrinhas grandes", "500g de carne moída", "1 lata de molho de tomate", "200g de queijo mussarela fatiado", "100g de queijo parmesão ralado", "sal, pimenta, orégano a gosto"),
            modoPreparo = listOf("Fatie as abobrinhas em lâminas finas no sentido do comprimento.", "Refogue a carne moída e adicione o molho de tomate, cozinhando por 10 minutos.", "Em um refratário, intercale camadas de abobrinha, molho de carne e queijo mussarela.", "Finalize com queijo parmesão ralado.", "Leve ao forno pré-aquecido a 180°C por 25-30 minutos, ou até dourar."),
            tempoPreparo = "45 min",
            porcoes = 4
        ),
        Receita(
            id = 5,
            nome = "Torta de Frango Fit com Aveia",
            descricaoCurta = "Uma torta prática e rica em fibras.",
            imagemUrl = R.drawable.torta_de_frango_fit_com_aveia,
            ingredientes = listOf("2 xícaras de frango desfiado", "1 cebola picada", "1 tomate picado", "1/2 xícara de aveia em flocos finos", "2 ovos", "1 xícara de leite desnatado", "temperos a gosto"),
            modoPreparo = listOf("Refogue a cebola e o tomate, adicione o frango desfiado e tempere.", "Em outro recipiente, misture a aveia, os ovos e o leite, formando a massa.", "Unte uma forma e despeje metade da massa, coloque o recheio de frango e cubra com o restante da massa.", "Leve ao forno pré-aquecido a 180°C por aproximadamente 30 minutos ou até dourar."),
            tempoPreparo = "40 min",
            porcoes = 6
        ),
        Receita(
            id = 6,
            nome = "Omelete de Legumes com Queijo Cottage",
            descricaoCurta = "Rápido, nutritivo e ideal para qualquer refeição.",
            imagemUrl = R.drawable.omelete_de_legumes_com_queijo_cottage,
            ingredientes = listOf("3 ovos", "1/4 xícara de pimentão picado", "1/4 xícara de espinafre", "2 colheres de sopa de queijo cottage", "sal e pimenta"),
            modoPreparo = listOf("Bata os ovos com sal e pimenta.", "Em uma frigideira antiaderente, refogue os legumes por alguns minutos.", "Despeje os ovos batidos sobre os legumes na frigideira.", "Quando a omelete estiver quase pronta, adicione o queijo cottage.", "Dobre a omelete ao meio e sirva."),
            tempoPreparo = "10 min",
            porcoes = 1
        ),
        Receita(
            id = 7,
            nome = "Creme de Abóbora com Gengibre",
            descricaoCurta = "Sopa cremosa e reconfortante para dias frios.",
            imagemUrl = R.drawable.creme_de_ab_bora_com_gengibre,
            ingredientes = listOf("500g de abóbora picada", "1 cebola", "2 dentes de alho", "1 pedaço pequeno de gengibre", "caldo de legumes", "creme de leite light (opcional)"),
            modoPreparo = listOf("Refogue cebola, alho e gengibre em uma panela.", "Adicione a abóbora e o caldo de legumes. Cozinhe até a abóbora ficar macia.", "Bata tudo no liquidificador até obter um creme liso.", "Volte à panela, tempere e, se desejar, adicione creme de leite light."),
            tempoPreparo = "30 min",
            porcoes = 3
        ),
        Receita(
            id = 8,
            nome = "Wrap Integral de Atum",
            descricaoCurta = "Uma refeição leve e rápida para o almoço ou lanche.",
            imagemUrl = R.drawable.wrap_integral_de_atum,
            ingredientes = listOf("1 lata de atum light", "1/4 xícara de milho", "2 colheres de sopa de iogurte natural", "folhas de alface", "1 tortilha integral"),
            modoPreparo = listOf("Escorra o atum e misture com o milho e o iogurte.", "Espalhe a mistura de atum sobre a tortilha.", "Adicione as folhas de alface.", "Enrole firmemente e corte ao meio."),
            tempoPreparo = "10 min",
            porcoes = 1
        ),
        Receita(
            id = 9,
            nome = "Bolinhos de Bacalhau Fit Assados",
            descricaoCurta = "Aproveite o sabor do bacalhau de forma mais saudável.",
            imagemUrl = R.drawable.bolinhos_de_bacalhau_fit_assados,
            ingredientes = listOf("250g de bacalhau dessalgado e desfiado", "1 batata grande cozida e amassada", "1 ovo", "cebolinha picada", "sal e pimenta do reino"),
            modoPreparo = listOf("Misture o bacalhau desfiado, a batata amassada, o ovo e a cebolinha.", "Tempere a gosto. Molde pequenos bolinhos.", "Arrume os bolinhos em uma assadeira antiaderente.", "Asse em forno pré-aquecido a 200°C por 20-25 minutos ou até dourarem."),
            tempoPreparo = "40 min",
            porcoes = 3
        ),
        Receita(
            id = 10,
            nome = "Mingau de Aveia com Frutas e Sementes",
            descricaoCurta = "Um café da manhã nutritivo e que sacia.",
            imagemUrl = R.drawable.mingau_de_aveia_com_frutas_e_sementes,
            ingredientes = listOf("1/2 xícara de aveia em flocos", "1 xícara de leite (vegetal ou desnatado)", "frutas picadas (banana, maçã, berries)", "sementes de chia ou linhaça", "mel ou canela a gosto"),
            modoPreparo = listOf("Em uma panela, misture a aveia e o leite. Leve ao fogo médio, mexendo sempre, até engrossar.", "Despeje em uma tigela. Adicione as frutas picadas e as sementes.", "Adoce com mel ou polvilhe canela, se desejar."),
            tempoPreparo = "10 min",
            porcoes = 1
        ),
        Receita(
            id = 11,
            nome = "Salmão Grelhado com Aspargos",
            descricaoCurta = "Prato elegante e rico em ômega-3.",
            imagemUrl = R.drawable.salm_o_grelhado_com_aspargos,
            ingredientes = listOf("1 filé de salmão", "1 maço de aspargos", "azeite", "suco de limão", "sal e pimenta do reino"),
            modoPreparo = listOf("Tempere o salmão com sal, pimenta e limão.", "Corte a parte dura dos aspargos e tempere com azeite, sal e pimenta.", "Grelhe o salmão e os aspargos em uma frigideira quente por alguns minutos de cada lado, até ficarem no ponto desejado."),
            tempoPreparo = "20 min",
            porcoes = 1
        ),
        Receita(
            id = 12,
            nome = "Sanduíche Natural de Peito de Peru",
            descricaoCurta = "Opção leve e rápida para lanches.",
            imagemUrl = R.drawable.sandu_che_natural_de_peito_de_peru,
            ingredientes = listOf("2 fatias de pão integral", "2 fatias de peito de peru", "1 fatia de queijo branco light", "alface e tomate", "creme de ricota light"),
            modoPreparo = listOf("Passe o creme de ricota em uma fatia de pão.", "Arrume o peito de peru, queijo, alface e tomate.", "Cubra com a outra fatia de pão e sirva."),
            tempoPreparo = "5 min",
            porcoes = 1
        ),
        Receita(
            id = 13,
            nome = "Salada de Grão de Bico com Temperos",
            descricaoCurta = "Uma salada proteica e vegetariana.",
            imagemUrl = R.drawable.salada_de_gr_o_de_bico_com_temperos,
            ingredientes = listOf("1 xícara de grão de bico cozido", "1/2 cebola roxa picada", "1/4 xícara de salsinha picada", "suco de 1 limão", "azeite, sal e cominho"),
            modoPreparo = listOf("Em uma tigela, misture o grão de bico, cebola e salsinha.", "Regue com azeite e suco de limão. Tempere com sal e cominho.", "Misture bem e deixe descansar por 10 minutos antes de servir para apurar o sabor."),
            tempoPreparo = "15 min",
            porcoes = 2
        ),
        Receita(
            id = 14,
            nome = "Pipoca Saudável na AirFryer",
            descricaoCurta = "Snack crocante e sem óleo.",
            imagemUrl = R.drawable.pipoca_saud_vel_na_airfryer,
            ingredientes = listOf("1/2 xícara de milho para pipoca", "água (opcional)", "sal a gosto"),
            modoPreparo = listOf("Coloque o milho na cesta da AirFryer (opcionalmente borrife um pouco de água).", "Ligue a AirFryer a 200°C por cerca de 10-15 minutos, sacudindo a cesta ocasionalmente.", "Retire quando os estouros diminuírem. Tempere com sal."),
            tempoPreparo = "15 min",
            porcoes = 2
        ),
        Receita(
            id = 15,
            nome = "Arroz Integral com Brócolis e Cenoura",
            descricaoCurta = "Um acompanhamento nutritivo e colorido.",
            imagemUrl = R.drawable.arroz_integral_com_br_colis_e_cenoura,
            ingredientes = listOf("1 xícara de arroz integral", "2 xícaras de água", "1/2 xícara de brócolis picado", "1/4 xícara de cenoura ralada", "sal"),
            modoPreparo = listOf("Lave o arroz integral.", "Em uma panela, refogue o arroz, brócolis e cenoura por alguns minutos.", "Adicione a água e o sal. Cozinhe em fogo baixo com a panela semi-tampada até a água secar e o arroz ficar macio."),
            tempoPreparo = "35 min",
            porcoes = 3
        )
    )

    val listaDeFavoritosMock = mutableListOf<Receita>()



    val listaDePerguntasFrequentes = listOf(
        FAQItem(
            pergunta = "Como adicionar uma receita aos favoritos?",
            resposta = "Na tela de detalhes de qualquer receita, toque no ícone de coração. Se estiver preenchido, a receita está nos favoritos; se estiver contornado, toque para adicionar."
        ),
        FAQItem(
            pergunta = "Onde encontro as configurações do app?",
            resposta = "As configurações do aplicativo podem ser acessadas através do botão 'Configurações' na barra de navegação inferior."
        ),
        FAQItem(
            pergunta = "Como pesquisar por uma receita?",
            resposta = "Utilize o botão 'Buscar' na barra de navegação inferior. Você pode digitar palavras-chave ou ingredientes para encontrar receitas específicas."
        ),
        FAQItem(
            pergunta = "Posso filtrar receitas por tipo de dieta?",
            resposta = "No momento, essa funcionalidade de filtro avançado ainda não está disponível, mas estamos trabalhando para implementá-la em futuras atualizações."
        ),
        FAQItem(
            pergunta = "Como funciona a funcionalidade de lista de compras?",
            resposta = "Esta funcionalidade está em desenvolvimento. Em breve, você poderá adicionar ingredientes diretamente das receitas para sua lista de compras."
        ),
        FAQItem(
            pergunta = "Existem opções para receitas vegetarianas ou veganas?",
            resposta = "Atualmente, você pode usar a barra de pesquisa para tentar encontrar receitas com termos como 'vegetariana' ou 'vegana'. Filtros específicos para dietas serão adicionados em breve."
        ),
        FAQItem(
            pergunta = "Como faço para relatar um erro ou dar um feedback?",
            resposta = "Na tela de detalhes de qualquer receita, clique no ícone de três pontos (menu) no canto superior direito e selecione 'Reportar Erro'. Para feedback geral, você pode usar as opções de contato na tela de Configurações."
        ),
        FAQItem(
            pergunta = "O aplicativo funciona offline?",
            resposta = "Não, o aplicativo requer conexão com a internet para carregar as receitas, imagens e modos de preparo. A funcionalidade offline para receitas salvas pode ser considerada para futuras versões."
        ),
        FAQItem(
            pergunta = "Posso personalizar minhas preferências alimentares?",
            resposta = "No momento, a personalização de preferências alimentares avançada não está disponível. Você pode usar a busca por enquanto e aguardar futuras atualizações que trarão mais opções de personalização."
        ),
        FAQItem(
            pergunta = "As receitas são verificadas por nutricionistas?",
            resposta = "Não, as receitas disponíveis no aplicativo são de fontes variadas e não são verificadas ou endossadas por nutricionistas. Consulte um profissional de saúde ou nutricionista para orientações dietéticas específicas para suas necessidades."
        ),
        FAQItem(
            pergunta = "Como eu atualizo meus dados pessoais?",
            resposta = "Este aplicativo não coleta ou armazena dados pessoais que precisem ser atualizados. Ele funciona com dados locais e não possui perfis de usuário ou sistema de login."
        ),
        FAQItem(
            pergunta = "Existe uma seção de receitas mais populares?",
            resposta = "Atualmente, não há uma seção dedicada a receitas mais populares ou em alta. As receitas são exibidas em uma lista geral, mas esta é uma ótima sugestão para futuras melhorias!"
        ),
        FAQItem(
            pergunta = "Como compartilho uma receita com amigos?",
            resposta = "Na tela de detalhes de qualquer receita, clique no ícone de três pontos (menu) no canto superior direito e selecione 'Compartilhar Receita'. Você poderá escolher entre diversos aplicativos de comunicação."
        )
    )
}