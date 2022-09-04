from mesa import Model, Agent
from mesa.space import MultiGrid
from mesa.time import RandomActivation
from mesa.datacollection import DataCollector
from agents import *
from .parameters import EcosystemParameters

class EcosystemModel(Model):
    def __init__(self, parameters: EcosystemParameters, initial_predator_amount: int, initial_prey_amount: int, initial_vegetation_amount: int, seed=None):
        self.parameters = parameters

        self.initial_predator_amount = initial_predator_amount
        self.initial_prey_amount = initial_prey_amount
        self.initial_vegetation_amount = initial_vegetation_amount

        self.predator_amount = 0
        self.prey_amount = 0
        self.vegetation_amount = 0

        self.current_agent_id = -1

        self.grid = MultiGrid(self.parameters.grid_width, self.parameters.grid_height, True)
        self.schedule = RandomActivation(self)

        self.populate()

        self.running = True

        self.datacollector = DataCollector(
            model_reporters={
                "predator_amount": "predator_amount",
                "prey_amount": "prey_amount",
                "vegetation_amount": "vegetation_amount",
                "mean_predator_age": lambda _: self.calculate_mean_age(PredatorAgent),
                "mean_prey_age": lambda _: self.calculate_mean_age(PreyAgent),
                "mean_predator_energy": lambda _: self.calculate_mean_energy(PredatorAgent),
                "mean_prey_energy": lambda _: self.calculate_mean_energy(PreyAgent)
            },
            agent_reporters={
                "type": lambda agent: agent.agent_type.value,
                "energy": "energy"
            }
        )
        self.data_collector = self.datacollector

        self.agents_to_forget = set()

    def only_vegetation(self):
        return self.predator_amount == 0 and self.prey_amount == 0

    def calculate_mean_energy(self, agent_type):
        count = 0; total = 0
        for agent in self.schedule.agents:
            if agent.__class__ == agent_type:
                total += agent.energy; count += 1
        return total / count if count else 0
    
    def calculate_mean_age(self, agent_type):
        count = 0; total = 0
        for agent in self.schedule.agents:
            if agent.__class__ == agent_type:
                total += agent.age; count += 1
        return total / count if count else 0
    
    def generate_agent_id(self):
        self.current_agent_id += 1
        return self.current_agent_id
    
    def place_agent_in_random_position(self, agent: Agent):
        x = self.random.randrange(self.grid.width)
        y = self.random.randrange(self.grid.height)
        self.grid.place_agent(agent, (x, y))

    def index_agent(self, agent: Agent, position=None):
        self.schedule.add(agent)
        if position is None: self.place_agent_in_random_position(agent)
        else: self.grid.place_agent(agent, position)

    def forget_agent(self, agent: PassiveBeingAgent):
        agent.die()
        self.schedule.remove(agent)
        self.grid.remove_agent(agent)

    def add_agent_to_forget(self, agent: PassiveBeingAgent):
        self.agents_to_forget.add(agent)
    
    def forget_agents(self):
        for agent in self.agents_to_forget:
            self.forget_agent(agent)
        self.agents_to_forget.clear()

    def populate(self):
        # creating predators
        for _ in range(self.initial_predator_amount):
            self.index_agent(PredatorAgent(self.generate_agent_id(), self, self.parameters.predator_parameters))

        # creating prey
        for _ in range(self.initial_prey_amount):
            self.index_agent(PreyAgent(self.generate_agent_id(), self, self.parameters.prey_parameters))

        self.populate_vegetation()
    
    def populate_vegetation(self):
        # creating vegetation, so that it always resets to the original amount
        for _ in range(self.initial_vegetation_amount - self.vegetation_amount):
            self.index_agent(VegetationAgent(self.generate_agent_id(), self, self.parameters.vegetation_parameters))
    
    def generate_offsprint(self, parent):
        self.index_agent(parent.__class__(self.generate_agent_id(), self, parent.parameters), parent.pos)

    def step(self):
        self.datacollector.collect(self)
        self.forget_agents()
        self.schedule.step()
        self.populate_vegetation()
        self.running = not self.only_vegetation()

