## Happy Places App

- Aplicativo para salvar localizações favoritas;

- Faz o uso de third party libraries e várias outras funcionalidades que serão citadas futuramente.

- A primeira delas é a Dexter, que simplifica a questão de pedir permissões para o aplicativo. Neste caso, há as permissões para acessar a galeria e a câmera.

- Essa aplicação também faz o uso de Room, para armazenar os Happy Places de maneira definitiva, em uma base de dados local.

  - E utiliza RecyclerView para mostrar os dados armazenados, ou melhor, os Happy Places salvos.

- Há o uso de uma outra third party library que é a [CircleImageView](https://github.com/hdodenhof/CircleImageView), que permite mostrar a imagem salva em um formato circular, de uma maneira muito simples.

- Para editar e deletar os Happy Places, foi implementado uma funcionalidade de deslizar para a esquerda o item que se deseja deletar ou editar, revelando dois botões para tais ações.
  A biblioteca usada no projeto é a [SwipeRevealLayout](https://github.com/chthai64/SwipeRevealLayout)
