using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Frames
{
    internal class Frame : IFrame
    {

        int countDoors;
        int countWheels;
        IWheel[] wheels;
        IDoor[] doors;
        string name;
        string status;
        protected ICheck check;
        protected bool unlimitedDoorsAndWheels;
        public Frame(string name, int countDoors, int countWheels, ICheck check, bool doorsAndWheelsUnlimited = false)
        {
            this.name = name;
            this.check = check;
            this.countDoors = countDoors;
            this.countWheels = countWheels;
            unlimitedDoorsAndWheels = doorsAndWheelsUnlimited;
            status = "";
            wheels = Array.Empty<IWheel>();
            doors = Array.Empty<IDoor>();
        }

        public int GetCountDoors()
        {
            return countDoors;
        }

        public int GetCountWheels()
        {
            return countWheels;
        }

        public IDoor[] GetDoors()
        {
            return doors;
        }
        public IWheel[] GetWheels()
        {
            return wheels;
        }

        public string GetName()
        {
            return name;
        }

        public virtual void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[]
            {
                "wheels",
                "doors"
            });
            wheels = (IWheel[])args["wheels"];
            doors = (IDoor[])args["doors"];
            try
            {
                check.CheckFrames(countWheels, countDoors, wheels, doors, unlimitedDoorsAndWheels);
            }
            catch (Exception e)
            {
                status = e.Message;
            }
        }

        public string GetStatus()
        {
            return status;
        }

        public bool getUnlimitedDoorsAndWheels()
        {
            return unlimitedDoorsAndWheels;
        }
    }
}
