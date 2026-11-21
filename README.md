# JoJo's Bizarre Mod (NeoForge 1.21.1)

## Como compilar no GitHub
1. Crie um repositorio e envie todo o conteudo desta pasta (inclusive `.github/`).
2. Aba **Actions** -> workflow "Build mod" roda sozinho a cada push.
3. Ao terminar, baixe o `.jar` em **Artifacts** (`jojomod-jar`) e coloque na pasta `mods` do NeoForge 21.1.x.

## Controles (configuraveis em Opcoes > Controles > JoJo Mod)
- **Z** invoca/recolhe o Stand
- **R** / **X** habilidade 1 / 2 (Stand se invocado; senao Hamon; senao Vampiro)

## Itens
- Flecha de Stand (baus de estruturas + comerciante ambulante) -> Stand aleatorio
- Mascara de Pedra (baus raros + comerciante ambulante) -> vira vampiro
- Pergaminho do Hamon (craft: livro + girassol + barra de ouro + po de glowstone) -> vira usuario de Hamon

## Comandos de teste (OP)
/jojo stand <id> | /jojo hamon | /jojo vampire | /jojo reset

## Config
`config/jojomod-common.toml`: chance de a flecha matar, vampiros queimarem no sol.

## Adicionar um Stand novo
Em `Stands.java`: registre em `static {}` com `add(new StandDef(...))`, adicione o `case` em `use(...)` e crie o metodo da habilidade.
