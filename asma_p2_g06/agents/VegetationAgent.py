from .BeingAgent import PassiveBeingAgent
from .AgentType import AgentType

class VegetationAgent(PassiveBeingAgent):
    def __init__(self, unique_id, model, parameters):
        PassiveBeingAgent.__init__(self, unique_id, model, parameters)
        model.vegetation_amount += 1
        
        self.agent_type = AgentType.Vegetation

    def die(self):
        self.model.vegetation_amount -= 1
