# Wrestling
Wrestling booking simulation


About the structure as of 2019-01-14

/model
- game models

/model/controller

GameController - The main game loop and high level game functions

PromotionController - Most AI goes here

/model/manager
- Classes that control lists of models (titles, workers, contracts, etc.)

/model/modelView
- main complex models used for game functions

/model/segmentEnum
- Types for segments and models

/view/
- ui
- typically each component is an fxml file with an accompanying controller java class
