using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces
{
    internal class Check : ICheck
    {
        public void CheckWheels(float max_pressure, float min_pressure,float this_pressure)
        {
            if(max_pressure<this_pressure)
            {
                throw new Exception("Колесо лопнуло!");
            }
            else if(min_pressure>this_pressure)
            {
                throw new Exception("Колесо сдуто!");
            }
        }
        public void CheckFrames(int countWheels, int countDoors, IWheel[] wheels, IDoor[] doors, bool doorsAndWheelsUnlimited)
        {
            string statusWheels = "none";
            string statusDoors = "none";
            if(countWheels < wheels.Count() && !doorsAndWheelsUnlimited)
            {
                statusWheels = "много";
            }
            else if(countWheels>wheels.Count())
            {
                statusWheels = "много";
            }

            if (countDoors < doors.Count() && !doorsAndWheelsUnlimited)
            {
                statusWheels = "много";
            }
            else if (countDoors > doors.Count())
            {
                statusWheels = "много";
            }

            if(statusWheels != "none" && statusDoors != "none")
            {
                throw new Exception("Слишком "+statusWheels + " колёс и слишком "+statusDoors + " дверей!");
            }
            else if(statusWheels == "none" && statusDoors != "none")
            {
                throw new Exception("Слишком " + statusDoors + " дверей!");
            }
            else if(statusWheels != "none" && statusDoors == "none")
            {
                throw new Exception("Слишком " + statusWheels + " колёс!");
            }

        }

        public void CheckEngine(float max_power, float min_power, float power)
        {
            if (max_power < power)
            {
                throw new Exception("Двигатель взорвался!");
            }
            else if(min_power > power)
            {
                throw new Exception("Двигатель мёртв!");
            }
        }

        public void CheckBottom(int max_volume, int min_volume,int volume)
        {
            if (max_volume < volume)
            {
                throw new Exception("Лотка утонула :(!");
            }
            else if (min_volume > volume)
            {
                throw new Exception("Эммм... Лодки не существует :)");
            }
        }

        public void CheckDictionary(IDictionary<string, object> values, string[] keys)
        {
            
            foreach(string key in keys)
            {
                if(!values.ContainsKey(key))
                {
                    throw new KeyNotFoundException();
                }
            }
        }
    }
}
