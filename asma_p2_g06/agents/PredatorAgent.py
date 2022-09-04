from .AgentType import AgentType
from .BeingAgent import ActiveBeingAgent

class PredatorAgent(ActiveBeingAgent):
    def __init__(self, unique_id, model, parameters):
        ActiveBeingAgent.__init__(self, unique_id, model, parameters)
        model.predator_amount += 1

        self.agent_type = AgentType.Predator

    def die(self):
        self.model.predator_amount -= 1

    def step(self):
        self.increment_attributes()
        if self.is_dead(): return

        self.move(AgentType.Predator, AgentType.Prey)
        cellmates = self.get_agents_in_same_cell()

        self.eat_first_alive_agent(cellmates[AgentType.Prey])
        self.mate_with_first_free_agent(cellmates[AgentType.Predator])
