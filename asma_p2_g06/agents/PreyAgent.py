from .BeingAgent import ActiveBeingAgent
from .AgentType import AgentType

class PreyAgent(ActiveBeingAgent):
    def __init__(self, unique_id, model, parameters):
        ActiveBeingAgent.__init__(self, unique_id, model, parameters)
        model.prey_amount += 1
        
        self.agent_type = AgentType.Prey

    def die(self):
        self.model.prey_amount -= 1

    def step(self):
        self.increment_attributes()
        if self.is_dead(): return

        self.move(AgentType.Prey, AgentType.Vegetation)
        cellmates = self.get_agents_in_same_cell()

        if len(cellmates[AgentType.Predator]) != 0: return

        self.eat_first_alive_agent(cellmates[AgentType.Vegetation])
        self.mate_with_first_free_agent(cellmates[AgentType.Prey])